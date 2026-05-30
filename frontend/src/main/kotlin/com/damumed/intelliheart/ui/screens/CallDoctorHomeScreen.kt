package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.FamilyViewModel
import com.damumed.intelliheart.viewmodel.HomeDoctorViewModel

/**
 * Экран вызова врача на дом
 * Позволяет пациенту заполнить форму с описанием симптомов и адресом
 */
@Composable
fun CallDoctorHomeScreen(
    session: AuthSession? = null,
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var address by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var selectedFamilyMember by remember { mutableStateOf<com.damumed.intelliheart.network.dto.PatientResponse?>(null) }

    val familyViewModel: FamilyViewModel = viewModel()
    val familyState = familyViewModel.state.value
    val homeDoctorViewModel: HomeDoctorViewModel = viewModel()
    val homeDoctorState = homeDoctorViewModel.state.value

    LaunchedEffect(session?.patientId) {
        session?.patientId?.let { familyViewModel.loadFamilyMembers(it) }
    }
    val currentPatientId = selectedFamilyMember?.id ?: session?.patientId
    LaunchedEffect(currentPatientId) {
        currentPatientId?.let { homeDoctorViewModel.loadRequests(it) }
    }

    // Список доступных симптомов на казахском
    val symptoms = listOf(
        "Қызба",             // Лихорадка
        "Бас ауруы",          // Головная боль
        "Жөтел",             // Кашель
        "Ауыздың құрғауы",   // Сухость во рту
        "Әлсіздік",          // Слабость
        "Жүрек айну",        // Тошнота
        "Кеуде ауыруы"       // Боль в груди
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок с кнопкой Назад
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Артқа",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Дәрігерді үйге шақыру",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        // Информационная карточка
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Үйге",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Біздің дәрігеріңіз сіздің үйіңізге келіп, толық консультация жүргізеді",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Раздел: Симптомы
        Text(
            text = "Симптомдарыңыз:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = selectedSymptoms.contains(symptom),
                            onCheckedChange = { isChecked ->
                                selectedSymptoms = if (isChecked) {
                                    selectedSymptoms + symptom
                                } else {
                                    selectedSymptoms - symptom
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = symptom,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Раздел: Адрес
        Text(
            text = "Мекенжайыңыз:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Облыс, қала, көше, үй нөмірі") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (session?.patientId != null && familyState.familyMembers.isNotEmpty()) {
            Text(
                text = "Кім үшін шақырамыз?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
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

        // Кнопка отправки
        Button(
            onClick = {
                if (currentPatientId != null && address.isNotEmpty() && selectedSymptoms.isNotEmpty()) {
                    homeDoctorViewModel.createRequest(
                        patientId = currentPatientId,
                        symptoms = selectedSymptoms.toList(),
                        address = address
                    ) {
                        showSuccess = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = currentPatientId != null &&
                address.isNotEmpty() &&
                selectedSymptoms.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Өтінім",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Өтінім қалдыру",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (homeDoctorState.error != null) {
            Text(
                text = homeDoctorState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text = "Өтінімдер тарихы",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        when {
            homeDoctorState.isLoading -> {
                CircularProgressIndicator()
            }
            homeDoctorState.requests.isEmpty() -> {
                Text(
                    text = "Өтінімдер жоқ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                homeDoctorState.requests.forEach { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Статус: ${request.status}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = request.address,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = request.symptoms.joinToString(", "),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog успеха
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Өтінім қабылданды") },
            text = {
                Text(
                    "Сіздің өтініміңіз қабылданды. Дәрігер 30 минут ішінде сізге хабарласады.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccess = false
                        onSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Түсінікті")
                }
            }
        )
    }
}
