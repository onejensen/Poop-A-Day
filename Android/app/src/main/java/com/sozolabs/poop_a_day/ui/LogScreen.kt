package com.sozolabs.poop_a_day.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sozolabs.poop_a_day.R
import com.sozolabs.poop_a_day.data.PoopLog
import com.sozolabs.poop_a_day.data.PoopRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(repository: PoopRepository, isDarkMode: Boolean = false) {
    val logs = remember { mutableStateOf<List<PoopLog>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logs.value = repository.getLogs().sortedByDescending { it.timestamp }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TileBackground(isDarkMode = isDarkMode)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.history_title), fontSize = 24.sp)
                Spacer(modifier = Modifier.weight(1f))
                if (logs.value.isNotEmpty()) {
                    IconButton(onClick = { showDeleteAllDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }

            LazyColumn {
                items(logs.value, key = { it.timestamp }) { log ->
                    LogItem(
                        log = log,
                        onDelete = {
                            scope.launch {
                                repository.deleteLog(log)
                                logs.value = repository.getLogs().sortedByDescending { it.timestamp }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(R.string.delete_all_title)) },
            text = { Text(stringResource(R.string.delete_all_message)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.deleteAllLogs()
                        logs.value = emptyList()
                    }
                    showDeleteAllDialog = false
                }) {
                    Text(stringResource(R.string.delete_all_confirm), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun LogItem(log: PoopLog, onDelete: () -> Unit) {
    val date = Date(log.timestamp)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = dateFormat.format(date), modifier = Modifier.weight(1f))
        Text(text = timeFormat.format(date))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Gray)
        }
    }
}
