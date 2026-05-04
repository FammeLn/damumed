package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран профиля пользователя
 * Отображает информацию о пациенте и предоставляет опции выхода
 */
@Composable
fun ProfileScreen(onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Заголовок
        Text(
            text = "Жеке кабинет",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(top = 16.dp)
        )

        // Карточка профиля
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Аватар
                Surface(
                    modifier = Modifier
                        .size(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Профиль",
                        modifier = Modifier
                            .padding(16.dp)
                            .size(48.dp),
                        tint = Color(0xFF1565C0)
                    )
                }

                // Информация о пациенте
                Text(
                    text = "Марат Сәлімов",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )

                // Поле: ИИН
                ProfileInfoField(
                    label = "ИИН:",
                    value = "990612345678"
                )

                // Поле: Телефон
                ProfileInfoField(
                    label = "Телефон:",
                    value = "+7 (700) 999-88-77"
                )

                // Поле: Дата рождения
                ProfileInfoField(
                    label = "Туған күні:",
                    value = "12.06.1999"
                )

                // Поле: Жынысы
                ProfileInfoField(
                    label = "Жынысы:",
                    value = "Ер"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка выхода
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Шығу",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Компонент для отображения поля информации профиля
 *
 * @param label Название поля (на казахском)
 * @param value Значение поля
 */
@Composable
private fun ProfileInfoField(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF666666),
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF212121),
            modifier = Modifier.weight(0.6f)
        )
    }
}
