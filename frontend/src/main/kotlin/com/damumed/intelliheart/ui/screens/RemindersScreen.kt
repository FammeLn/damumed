package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.ReminderResponse
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.RemindersViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    session: AuthSession?,
    onBack: () -> Unit
) {
    val viewModel: RemindersViewModel = viewModel()
    val state = viewModel.state.value
    val patientId = session?.patientId

    var title by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(patientId) {
        patientId?.let { viewModel.loadReminders(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Еске салулар") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Артқа")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (patientId == null) {
                Text("Еске салу жасау үшін алдымен пациент профилін толтырыңыз.")
                return@Column
            }

            Text(
                text = "Жаңа еске салу",
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Тақырып") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateTime,
                onValueChange = { dateTime = it },
                label = { Text("Күні/уақыты (YYYY-MM-DDTHH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ескерту (міндетті емес)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val parsed = runCatching { LocalDateTime.parse(dateTime) }.getOrNull()
                    if (parsed != null) {
                        viewModel.createReminder(
                            patientId = patientId,
                            title = title,
                            scheduledAt = parsed,
                            note = note
                        ) {
                            title = ""
                            dateTime = ""
                            note = ""
                        }
                    }
                },
                enabled = title.isNotBlank() && dateTime.isNotBlank()
            ) {
                Text("Сақтау")
            }

            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                state.reminders.isEmpty() -> Text("Еске салулар жоқ")
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.reminders) { reminder ->
                            ReminderCard(reminder) {
                                viewModel.markDone(reminder.id, patientId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderResponse,
    onDone: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(reminder.title, fontWeight = FontWeight.SemiBold)
            Text(reminder.scheduledAt.toString(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            reminder.note?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (!reminder.isDone) {
                TextButton(onClick = onDone) { Text("Орындалды") }
            }
        }
    }
}
