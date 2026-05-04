package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Главный экран приложения (Басты бет)
 * Отображает приветственное сообщение и кнопку для вызова врача на дом
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Приветственный текст
        Text(
            text = "Қайырлы күн!",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Основное сообщение
        Text(
            text = "Сіздің денсаулығыңыз – біздің басты байлығымыз.",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(bottom = 48.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Кнопка для вызова врача на дом
        Button(
            onClick = { /* TODO: Реализовать логику вызова врача */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Дәрігерді үйге шақыру",
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Кнопка для записи к врачу в клинику
        Button(
            onClick = { /* TODO: Реализовать переход на экран записи */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Дәрігерге қайта",
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Кнопка для просмотра медицинской карты
        Button(
            onClick = { /* TODO: Реализовать переход на экран медицинской карты */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                text = "Медициналық картамды қарау",
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
