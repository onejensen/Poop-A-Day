package com.sozolabs.poop_a_day.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sozolabs.poop_a_day.R
import com.sozolabs.poop_a_day.data.PoopLog
import com.sozolabs.poop_a_day.data.PoopRepository
import java.util.Calendar

@Composable
fun StatsScreen(repository: PoopRepository, isDarkMode: Boolean = false) {
    val stats = remember { mutableStateOf(Stats()) }

    LaunchedEffect(Unit) {
        val logs = repository.getLogs()
        stats.value = calculateStats(logs)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TileBackground(isDarkMode = isDarkMode)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = stringResource(R.string.statistics_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

            StatCard(stringResource(R.string.stat_today), stats.value.today)
            StatCard(stringResource(R.string.stat_this_week), stats.value.week)
            StatCard(stringResource(R.string.stat_this_month), stats.value.month)
            StatCard(stringResource(R.string.stat_this_year), stats.value.year)
        }
    }
}

@Composable
fun StatCard(label: String, count: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 18.sp)
            Text(text = count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

data class Stats(
    val today: Int = 0,
    val week: Int = 0,
    val month: Int = 0,
    val year: Int = 0
)

fun calculateStats(logs: List<PoopLog>): Stats {
    val now = Calendar.getInstance()
    var today = 0
    var week = 0
    var month = 0
    var year = 0

    logs.forEach { log ->
        val logTime = Calendar.getInstance()
        logTime.timeInMillis = log.timestamp

        if (logTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            year++
            if (logTime.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                month++
                if (logTime.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)) {
                    week++
                    if (logTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                        today++
                    }
                }
            }
        }
    }
    return Stats(today, week, month, year)
}
