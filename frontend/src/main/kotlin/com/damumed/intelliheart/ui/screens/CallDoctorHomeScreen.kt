package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран вызова врача на дом
 * Позволяет пациенту заполнить форму с описанием симптомов и адресом
 */
@Composable
fun CallDoctorHomeScreen(onSuccess: () -> Unit = {}) {
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var address by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    // Список доступных симптомов на казахском
    val symptoms = listOf(
        "Өтке",           // Лихорадка
        "Бас ауруы",      // Головная боль
        "Қоқылту",        // Кашель
        "Құрғақ аузы",    // Сухость во рту
        "Томалық",        // Слабость
        "Өңі іріңгей",    // Тошнота
        "Еңбек жүрегінде ауру" // Боль в груди
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Дәрігерді үйге шақыру",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Информационная карточка
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
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
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Біздің дәрігері сіздің үйіңізге келіп, толық консультация жүргізеді",
                    fontSize = 13.sp,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Раздел: Симптомы
        Text(
            text = "Симптомдарыңыз:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
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
                    .background(Color.White)
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
                                checkedColor = Color(0xFF1565C0)
                            )
                        )

                        Text(
                            text = symptom,
                            fontSize = 14.sp,
                            color = Color(0xFF212121)
                        )
                    }
                }
            }
        }

        // Раздел: Адрес
        Text(
            text = "Сіздің адресіңіз:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Өл. облысы, қаласы, көшесі, үй номері") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color(0xFFDDDDDD),
                focusedLabelColor = Color(0xFF1565C0)
            ),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка отправки
        Button(
            onClick = {
                if (address.isNotEmpty() && selectedSymptoms.isNotEmpty()) {
                    showSuccess = true
                    // Здесь можно добавить отправку на бэкенд
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = address.isNotEmpty() && selectedSymptoms.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0),
                disabledContainerColor = Color(0xFFCCCCCC)
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
    }

    // Dialog успеха
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Өтінім қабылданды") },
            text = {
                Text(
                    "Сіздің өтінімі қабылданды. Дәрігер 30 минут ішінде сізге хабарласады.",
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
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text("Түсінік")
                }
            }
        )
    }
}
