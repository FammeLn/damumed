package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.AppointmentBookingViewModel

@Composable
fun AppointmentBookingScreen(
    doctorId: Long,
    session: AuthSession?,
    onPatientCreated: (Long) -> Unit,
    onBookingSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: AppointmentBookingViewModel = viewModel()
    val state = viewModel.state.value

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    var iin by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }

    LaunchedEffect(doctorId) {
        if (doctorId > 0) {
            viewModel.loadDoctor(doctorId)
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

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Туған күні (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

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

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Күні (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Уақыты (HH:MM)") },
            modifier = Modifier.fillMaxWidth()
        )

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

        val patientId = session?.patientId
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
