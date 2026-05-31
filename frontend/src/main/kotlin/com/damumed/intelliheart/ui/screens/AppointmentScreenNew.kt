package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.DoctorResponse
import com.damumed.intelliheart.network.dto.AppointmentResponse
import com.damumed.intelliheart.viewmodel.DoctorListUiState
import com.damumed.intelliheart.viewmodel.DoctorsViewModel
import com.damumed.intelliheart.viewmodel.AppointmentsViewModel
import com.damumed.intelliheart.ui.auth.AuthSession
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.LocalDateTime

/**
 * Обновленный экран записи к врачу (Жазылу)
 * Отображает список врачей в виде красивых карточек с информацией и кнопкой записи
 */
@Composable
fun AppointmentScreen(
    modifier: Modifier = Modifier,
    onBookDoctor: (Long) -> Unit = {},
    session: AuthSession? = null
) {
    // Получаем ViewModel для управления состоянием списка врачей
    val viewModel: DoctorsViewModel = viewModel()
    val uiState = remember { viewModel.uiState }
    val appointmentsViewModel: AppointmentsViewModel = viewModel()
    val appointmentsState = appointmentsViewModel.state.value
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(session?.patientId) {
        session?.patientId?.let { appointmentsViewModel.loadAppointments(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок экрана
        Text(
            text = "Дәрігерге жазылу",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Дәрігерлер") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Менің жазылуларым") })
        }

        if (selectedTab == 0) {
            when (val state = uiState.value) {
                is DoctorListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is DoctorListUiState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.doctors) { doctor ->
                            DoctorCard(
                                doctor = doctor,
                                onBook = { onBookDoctor(doctor.id) }
                            )
                        }
                    }
                }
                is DoctorListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Деректерді жүктеу мүмкін болмады",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = { viewModel.loadDoctors() }) {
                                Text(text = "Қайта жүктеу")
                            }
                        }
                    }
                }
            }
        } else {
            if (session?.patientId == null) {
                Text(
                    text = "Жазылуларды көру үшін пациент профилін толтырыңыз.",
                    modifier = Modifier.padding(12.dp)
                )
            } else if (appointmentsState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp))
            } else if (appointmentsState.error != null) {
                Text(
                    text = appointmentsState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            } else if (appointmentsState.appointments.isEmpty()) {
                Text(
                    text = "Жазылулар жоқ",
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(appointmentsState.appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onCancel = {
                                session.patientId?.let { patientId ->
                                    appointmentsViewModel.cancelAppointment(appointment.id, patientId)
                                }
                            },
                            onReschedule = { newDateTime ->
                                session.patientId?.let { patientId ->
                                    appointmentsViewModel.rescheduleAppointment(
                                        appointment.id,
                                        patientId,
                                        newDateTime
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Компонент для отображения карточки врача
 * Показывает информацию о враче и кнопку для записи к нему
 */
@Composable
private fun DoctorCard(
    doctor: DoctorResponse,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Имя врача
            Text(
                text = doctor.fullName,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Специализация
            Text(
                text = "Мамандану: ${doctor.specialization}",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // Квалификация
            Text(
                text = "Біліктілігі: ${doctor.qualification}",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Информация о стаже и рейтинге в одной строке
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Тәжірибесі: ${doctor.experienceYears} жыл",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    text = "Рейтингі: ${String.format("%.1f", doctor.rating)}/5.0 (${doctor.ratingCount} пікір)",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Место работы
            Text(
                text = "Орны: ${doctor.workplace}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            )

            // Кнопка для записи к врачу
            Button(
                onClick = onBook,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Жазылу",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentCard(
    appointment: AppointmentResponse,
    onCancel: () -> Unit,
    onReschedule: (LocalDateTime) -> Unit
) {
    var rescheduleDate by remember { mutableStateOf("") }
    var rescheduleTime by remember { mutableStateOf("") }

    var showRescheduleDatePicker by remember { mutableStateOf(false) }
    var showRescheduleTimePicker by remember { mutableStateOf(false) }

    if (showRescheduleDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showRescheduleDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.of("UTC"))
                                .toLocalDate()
                            rescheduleDate = selectedLocalDate.toString()
                        }
                        showRescheduleDatePicker = false
                    }
                ) {
                    Text("Мақұлдау")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDatePicker = false }) {
                    Text("Бас тарту")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showRescheduleTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 12,
            initialMinute = 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showRescheduleTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        rescheduleTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showRescheduleTimePicker = false
                    }
                ) {
                    Text("Мақұлдау")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleTimePicker = false }) {
                    Text("Бас тарту")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = appointment.doctor.fullName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "${appointment.appointmentDateTime} • ${appointment.status}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCancel) { Text("Болдырмау") }
            }
            Text(
                text = "Ауыстыру",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showRescheduleDatePicker = true }
                ) {
                    OutlinedTextField(
                        value = rescheduleDate,
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showRescheduleTimePicker = true }
                ) {
                    OutlinedTextField(
                        value = rescheduleTime,
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
            }
            Button(
                onClick = {
                    val newDateTime = runCatching {
                        LocalDateTime.parse("${rescheduleDate}T${rescheduleTime}")
                    }.getOrNull()
                    if (newDateTime != null) {
                        onReschedule(newDateTime)
                        rescheduleDate = ""
                        rescheduleTime = ""
                    }
                },
                enabled = rescheduleDate.isNotBlank() && rescheduleTime.isNotBlank()
            ) {
                Text("Ауыстыру")
            }
        }
    }
}
