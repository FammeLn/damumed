package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.PatientResponse
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.FamilyViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMembersScreen(
    session: AuthSession?,
    onBack: () -> Unit,
    onPrimaryCreated: (Long) -> Unit
) {
    val viewModel: FamilyViewModel = viewModel()
    val state = viewModel.state.value
    val primaryId = session?.patientId ?: 0L

    LaunchedEffect(primaryId) {
        if (primaryId > 0) viewModel.loadFamilyMembers(primaryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Менің отбасым") },
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
        },
        floatingActionButton = {
            if (primaryId > 0) {
                FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "Қосу")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (primaryId == 0L) {
                PrimaryPatientForm(
                    isSaving = state.isSaving,
                    error = state.error,
                    onSave = { iin, fullName, dob, gender, phone, address, medicalHistory ->
                        viewModel.createPrimaryPatient(
                            iin = iin,
                            fullName = fullName,
                            dateOfBirth = dob,
                            gender = gender,
                            phoneNumber = phone,
                            address = address,
                            medicalHistory = medicalHistory
                        ) { patient ->
                            onPrimaryCreated(patient.id)
                        }
                    }
                )
            } else if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.familyMembers.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Отбасы мүшелері жоқ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "«+» түймесін басып қосыңыз",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.familyMembers) { member ->
                        FamilyMemberCard(member)
                    }
                }
            }
        }
    }

    // Диалог добавления члена семьи
    if (state.showAddDialog) {
        AddFamilyMemberDialog(
            isSaving = state.isSaving,
            error = state.error,
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { iin, fullName, dob, gender, phone, address, relationType, medicalHistory ->
                viewModel.addFamilyMember(
                    primaryPatientId = primaryId,
                    iin = iin,
                    fullName = fullName,
                    dateOfBirth = dob,
                    gender = gender,
                    phoneNumber = phone,
                    address = address,
                    relationType = relationType,
                    medicalHistory = medicalHistory,
                    onSuccess = {}
                )
            }
        )
    }
}

@Composable
fun FamilyMemberCard(member: PatientResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(member.fullName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    text = buildString {
                        member.relationType?.let { append("$it • ") }
                        append("ИИН: ${member.iin}")
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Туған күні: ${member.dateOfBirth}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberDialog(
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: (
        iin: String,
        fullName: String,
        dob: LocalDate,
        gender: String,
        phone: String,
        address: String,
        relationType: String,
        medicalHistory: String?
    ) -> Unit
) {
    var iin by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dobText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("М") }
    var relationType by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var dobError by remember { mutableStateOf(false) }

    val relationTypes = listOf("Ұлы", "Қызы", "Анасы", "Әкесі", "Жұбайы", "Басқа")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Отбасы мүшесін қосу") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Аты-жөні") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = iin,
                    onValueChange = { iin = it },
                    label = { Text("ИИН (12 сан)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dobText,
                    onValueChange = { dobText = it; dobError = false },
                    label = { Text("Туған күні (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = dobError,
                    supportingText = { if (dobError) Text("Дұрыс формат: 2005-03-15") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                // Выбор пола
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("М", "Ж").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(if (g == "М") "Ер" else "Әйел") }
                        )
                    }
                }
                // Тип родства
                Text("Туыстық дәрежесі:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    relationTypes.take(3).forEach { r ->
                        FilterChip(
                            selected = relationType == r,
                            onClick = { relationType = r },
                            label = { Text(r, fontSize = 12.sp) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    relationTypes.drop(3).forEach { r ->
                        FilterChip(
                            selected = relationType == r,
                            onClick = { relationType = r },
                            label = { Text(r, fontSize = 12.sp) }
                        )
                    }
                }
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dob = runCatching { LocalDate.parse(dobText) }.getOrNull()
                    if (dob == null) { dobError = true; return@Button }
                    onConfirm(iin, fullName, dob, gender, phone, address, relationType, medicalHistory)
                },
                enabled = !isSaving &&
                    iin.isNotBlank() &&
                    fullName.isNotBlank() &&
                    dobText.isNotBlank() &&
                    phone.isNotBlank() &&
                    address.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                else Text("Сақтау")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Бас тарту") }
        }
    )
}

@Composable
private fun PrimaryPatientForm(
    isSaving: Boolean,
    error: String?,
    onSave: (
        iin: String,
        fullName: String,
        dob: LocalDate,
        gender: String,
        phone: String,
        address: String,
        medicalHistory: String?
    ) -> Unit
) {
    var iin by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dobText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("М") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var dobError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Негізгі пациент профилін толтырыңыз",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Аты-жөні") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = iin,
            onValueChange = { iin = it },
            label = { Text("ИИН (12 сан)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = dobText,
            onValueChange = { dobText = it; dobError = false },
            label = { Text("Туған күні (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = dobError,
            supportingText = { if (dobError) Text("Дұрыс формат: 2005-03-15") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("М", "Ж").forEach { g ->
                FilterChip(
                    selected = gender == g,
                    onClick = { gender = g },
                    label = { Text(if (g == "М") "Ер" else "Әйел") }
                )
            }
        }
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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

        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Button(
            onClick = {
                val dob = runCatching { LocalDate.parse(dobText) }.getOrNull()
                if (dob == null) {
                    dobError = true
                    return@Button
                }
                onSave(iin, fullName, dob, gender, phone, address, medicalHistory)
            },
            enabled = !isSaving &&
                iin.isNotBlank() &&
                fullName.isNotBlank() &&
                dobText.isNotBlank() &&
                phone.isNotBlank() &&
                address.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text("Сақтау")
            }
        }
    }
}
