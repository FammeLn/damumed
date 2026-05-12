package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран медицинской карты (Медкарта)
 * Отображает историю болезни и результаты прошлых приемов
 */
@Composable
fun MedicalRecordScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Медициналық карта",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Последние осмотры
        Text(
            text = "Соңғы тексерулер",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Запись 1
        MedicalRecordCard(
            date = "2026-04-20",
            doctor = "Аяулы Ерлан",
            specialization = "Кардиолог",
            diagnosis = "Артериялық гипертензия (1 сатысы)",
            notes = "Қабылдау: күніне 2 рет."
        )

        // Запись 2
        MedicalRecordCard(
            date = "2026-03-15",
            doctor = "Нурай Қасымова",
            specialization = "Невролог",
            diagnosis = "Мигрень",
            notes = "Функционалды МРТ жасалды. Патология табылмады."
        )

        // Запись 3
        MedicalRecordCard(
            date = "2026-02-10",
            doctor = "Аяулы Ерлан",
            specialization = "Кардиолог",
            diagnosis = "Профилактикалық қабылдау",
            notes = "ЭКГ нормада. Келесі қабылдау 6 айдан кейін."
        )

        // Анализы
        Text(
            text = "Соңғы талдаулар",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        AnalysisCard(
            name = "Жалпы қан талдауы",
            date = "2026-04-18",
            status = "Норма ✓"
        )

        AnalysisCard(
            name = "Биохимиялық талдау",
            date = "2026-04-18",
            status = "Норма ✓"
        )

        AnalysisCard(
            name = "Несеп қышқылы талдауы",
            date = "2026-03-15",
            status = "Норма ✓"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Карточка медицинской записи
 */
@Composable
private fun MedicalRecordCard(
    date: String,
    doctor: String,
    specialization: String,
    diagnosis: String,
    notes: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = specialization,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Text(
                text = "Дәрігері: $doctor",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Диагноз: $diagnosis",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = notes,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * Карточка анализа
 */
@Composable
private fun AnalysisCard(
    name: String,
    date: String,
    status: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.FilePresent,
                contentDescription = "Анализ",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
