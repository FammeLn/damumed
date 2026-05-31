package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
    var selectedDate by remember { mutableStateOf<java.time.LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<java.time.LocalTime?>(null) }
    var note by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedDate = java.time.Instant.ofEpochMilli(millis)
                                        .atZone(java.time.ZoneId.of("UTC"))
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Мақұлдау")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Бас тарту")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                val timePickerState = rememberTimePickerState(
                    initialHour = 12,
                    initialMinute = 0,
                    is24Hour = true
                )
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedTime = java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }
                        ) {
                            Text("Мақұлдау")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Бас тарту")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }

            val dateText = selectedDate?.toString() ?: ""
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    label = { Text("Күні") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            val timeText = selectedTime?.let { String.format("%02d:%02d", it.hour, it.minute) } ?: ""
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true }
            ) {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = {},
                    label = { Text("Уақыты") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ескерту (міндетті емес)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (selectedDate != null && selectedTime != null) {
                        val parsed = LocalDateTime.of(selectedDate, selectedTime)
                        viewModel.createReminder(
                            patientId = patientId,
                            title = title,
                            scheduledAt = parsed,
                            note = note
                        ) {
                            title = ""
                            selectedDate = null
                            selectedTime = null
                            note = ""
                        }
                    }
                },
                enabled = title.isNotBlank() && selectedDate != null && selectedTime != null
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
