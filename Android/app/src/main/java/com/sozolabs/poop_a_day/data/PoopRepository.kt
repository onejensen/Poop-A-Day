package com.sozolabs.poop_a_day.data

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

data class PoopLog(
    val timestamp: Long = System.currentTimeMillis()
)

class PoopRepository(private val context: Context) {
    private val gson = Gson()
    private val fileName = "poop_logs.json"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    suspend fun addLog() {
        withContext(Dispatchers.IO) {
            val logs = getLogs().toMutableList()
            logs.add(PoopLog())
            val json = gson.toJson(logs)
            getFile().writeText(json)
        }
    }

    suspend fun getLogs(): List<PoopLog> {
        return withContext(Dispatchers.IO) {
            val file = getFile()
            if (!file.exists()) return@withContext emptyList()

            try {
                val json = file.readText()
                val type = object : TypeToken<List<PoopLog>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun deleteLog(log: PoopLog) {
        withContext(Dispatchers.IO) {
            val logs = getLogs().toMutableList()
            logs.removeAll { it.timestamp == log.timestamp }
            getFile().writeText(gson.toJson(logs))
        }
    }

    suspend fun deleteAllLogs() {
        withContext(Dispatchers.IO) {
            getFile().writeText("[]")
        }
    }

    suspend fun getStreak(): Int {
        val logs = getLogs()
        if (logs.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Get unique days with logs, sorted descending
        val uniqueDays = logs.map { log ->
            Calendar.getInstance().apply {
                timeInMillis = log.timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }.toSortedSet().sortedDescending()

        if (uniqueDays.isEmpty()) return 0

        // Check if most recent log is today or yesterday
        val yesterdayStart = Calendar.getInstance().apply {
            timeInMillis = todayStart.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        if (uniqueDays.first() != todayStart.timeInMillis && uniqueDays.first() != yesterdayStart) {
            return 0
        }

        var streak = 0
        var expectedDay = uniqueDays.first()

        for (day in uniqueDays) {
            if (day == expectedDay) {
                streak++
                expectedDay = Calendar.getInstance().apply {
                    timeInMillis = expectedDay
                    add(Calendar.DAY_OF_YEAR, -1)
                }.timeInMillis
            } else if (day < expectedDay) {
                break
            }
        }

        return streak
    }

    // Export data as JSON string
    suspend fun exportData(): String {
        val logs = getLogs()
        return gson.toJson(logs)
    }

    // Import data from URI
    suspend fun importData(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
                val json = inputStream.bufferedReader().readText()
                inputStream.close()
                val type = object : TypeToken<List<PoopLog>>() {}.type
                val logs: List<PoopLog> = gson.fromJson(json, type) ?: return@withContext false
                getFile().writeText(gson.toJson(logs))
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
