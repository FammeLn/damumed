package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.PatientResponse
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.AppointmentBookingViewModel
import com.damumed.intelliheart.viewmodel.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    doctorId: Long,
    session: AuthSession?,
    onPatientCreated: (Long) -> Unit,
    onBookingSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: AppointmentBookingViewModel = viewModel()
    val familyViewModel: FamilyViewModel = viewModel()
    val state = viewModel.state.value
    val familyState = familyViewModel.state.value

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    // Выбранный пациент: null = сам пользователь, иначе член семьи
    var selectedFamilyMember by remember { mutableStateOf<PatientResponse?>(null) }

    var iin by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }

    LaunchedEffect(doctorId) {
        if (doctorId > 0) viewModel.loadDoctor(doctorId)
        session?.patientId?.let { familyViewModel.loadFamilyMembers(it) }
    }

    LaunchedEffect(date, doctorId) {
        if (doctorId > 0 && date.isNotBlank()) {
            viewModel.loadSlots(doctorId, date)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Жазылу",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        state.doctor?.let { doctor ->
            Text(
                text = "${doctor.fullName} • ${doctor.specialization}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Блок выбора пациента
        if (session?.patientId != null && familyState.familyMembers.isNotEmpty()) {
            Text(
                text = "Кім үшін жазыламыз?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            // Кнопка "Өзім үшін"
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFamilyMember == null,
                    onClick = { selectedFamilyMember = null },
                    label = { Text("Өзім үшін") }
                )
                familyState.familyMembers.forEach { member ->
                    FilterChip(
                        selected = selectedFamilyMember?.id == member.id,
                        onClick = { selectedFamilyMember = member },
                        label = { Text(member.fullName.split(" ").firstOrNull() ?: member.fullName) }
                    )
                }
            }
        }

        if (session?.patientId == null) {
            Text(
                text = "Пациент мәліметтері",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Аты-жөні") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = iin,
                onValueChange = { iin = it },
                label = { Text("ИИН") },
                modifier = Modifier.fillMaxWidth()
            )

            var showDobDatePicker by remember { mutableStateOf(false) }
            if (showDobDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDobDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val selectedLocalDate = java.time.Instant.ofEpochMilli(millis)
                                        .atZone(java.time.ZoneId.of("UTC"))
                                        .toLocalDate()
                                    dateOfBirth = selectedLocalDate.toString()
                                }
                                showDobDatePicker = false
                            }
                        ) {
                            Text("Мақұлдау")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDobDatePicker = false }) {
                            Text("Бас тарту")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDobDatePicker = true }
            ) {
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {},
                    label = { Text("Туған күні (YYYY-MM-DD)") },
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
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Жынысы") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Мекенжай") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = medicalHistory,
                onValueChange = { medicalHistory = it },
                label = { Text("Қосымша мәлімет (міндетті емес)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.createPatient(
                        iin = iin,
                        fullName = fullName,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        phoneNumber = phoneNumber,
                        address = address,
                        medicalHistory = medicalHistory
                    ) { patient ->
                        onPatientCreated(patient.id)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading &&
                    iin.isNotBlank() &&
                    fullName.isNotBlank() &&
                    dateOfBirth.isNotBlank() &&
                    gender.isNotBlank() &&
                    phoneNumber.isNotBlank() &&
                    address.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    Text("Пациентті сақтау")
                }
            }
        }

        Text(
            text = "Қабылдау уақыты",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        var showBookingDatePicker by remember { mutableStateOf(false) }
        if (showBookingDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showBookingDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedLocalDate = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.of("UTC"))
                                    .toLocalDate()
                                date = selectedLocalDate.toString()
                            }
                            showBookingDatePicker = false
                        }
                    ) {
                        Text("Мақұлдау")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBookingDatePicker = false }) {
                        Text("Бас тарту")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBookingDatePicker = true }
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Күні (YYYY-MM-DD)") },
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

        if (state.availableSlots.isNotEmpty()) {
            Text(
                text = "Қолжетімді уақыт",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.availableSlots.take(4).forEach { slot ->
                    FilterChip(
                        selected = selectedTime == slot,
                        onClick = { selectedTime = slot; time = slot },
                        label = { Text(slot) }
                    )
                }
            }
            if (state.availableSlots.size > 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.availableSlots.drop(4).take(4).forEach { slot ->
                        FilterChip(
                            selected = selectedTime == slot,
                            onClick = { selectedTime = slot; time = slot },
                            label = { Text(slot) }
                        )
                    }
                }
            }
        } else {
            var showBookingTimePicker by remember { mutableStateOf(false) }
            if (showBookingTimePicker) {
                val timePickerState = rememberTimePickerState(
                    initialHour = 12,
                    initialMinute = 0,
                    is24Hour = true
                )
                AlertDialog(
                    onDismissRequest = { showBookingTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                                showBookingTimePicker = false
                            }
                        ) {
                            Text("Мақұлдау")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBookingTimePicker = false }) {
                            Text("Бас тарту")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBookingTimePicker = true }
            ) {
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text("Уақыты (HH:MM)") },
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

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            label = { Text("Себебі (міндетті емес)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.successMessage != null) {
            Text(
                text = state.successMessage,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val patientId = selectedFamilyMember?.id ?: session?.patientId
        Button(
            onClick = {
                if (patientId != null) {
                    viewModel.bookAppointment(
                        patientId = patientId,
                        doctorId = doctorId,
                        date = date,
                        time = time,
                        reason = reason,
                        onSuccess = onBookingSuccess
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = patientId != null && date.isNotBlank() && time.isNotBlank() && !state.isLoading
        ) {
            Text("Өтінімді жіберу")
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Артқа")
        }
    }
}
