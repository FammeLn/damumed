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
import androidx.compose.ui.graphics.Color
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
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Медициналық карта",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Последние осмотры
        Text(
            text = "Соңғы осмотрлар",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Запись 1
        MedicalRecordCard(
            date = "2026-04-20",
            doctor = "Аяулы Ерлан",
            specialization = "Кардиолог",
            diagnosis = "Артериялық гипертензия (1 сатысы)",
            notes = "Медикамент атауы өзгертілді. Қабылдау: 2 рет сайын бір жүйеде."
        )

        // Запись 2
        MedicalRecordCard(
            date = "2026-03-15",
            doctor = "Нурай Қасымова",
            specialization = "Невролог",
            diagnosis = "Мигрень",
            notes = "Функционалды МРТ атқарылды. Патология табылмады."
        )

        // Запись 3
        MedicalRecordCard(
            date = "2026-02-10",
            doctor = "Аяулы Ерлан",
            specialization = "Кардиолог",
            diagnosis = "Профилактикалық прием",
            notes = "ЭКГ норма бойынша. Келесі бірдей сайын 6 ай қойындағы."
        )

        // Анализы
        Text(
            text = "Соңғы талдаулар",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(top = 16.dp)
        )

        AnalysisCard(
            name = "Жалпы қан анализы",
            date = "2026-04-18",
            status = "Норма ✓"
        )

        AnalysisCard(
            name = "Биохимиялық талдау",
            date = "2026-04-18",
            status = "Норма ✓"
        )

        AnalysisCard(
            name = "Сірке тұзы анализы",
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
                .background(Color.White)
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
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = specialization,
                    fontSize = 12.sp,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Text(
                text = "Дәрігері: $doctor",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )

            Text(
                text = "Диагноз: $diagnosis",
                fontSize = 13.sp,
                color = Color(0xFF424242)
            )

            Text(
                text = notes,
                fontSize = 12.sp,
                color = Color(0xFF666666),
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
                .background(Color.White)
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
                    color = Color(0xFF212121)
                )

                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            Icon(
                imageVector = Icons.Filled.FilePresent,
                contentDescription = "Анализ",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

