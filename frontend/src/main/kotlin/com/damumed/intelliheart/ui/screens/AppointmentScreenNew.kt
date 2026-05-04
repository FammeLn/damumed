package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.DoctorResponse
import com.damumed.intelliheart.viewmodel.DoctorListUiState
import com.damumed.intelliheart.viewmodel.DoctorsViewModel

/**
 * Обновленный экран записи к врачу (Жазылу)
 * Отображает список врачей в виде красивых карточек с информацией и кнопкой записи
 */
@Composable
fun AppointmentScreen(modifier: Modifier = Modifier) {
    // Получаем ViewModel для управления состоянием списка врачей
    val viewModel: DoctorsViewModel = viewModel()
    val uiState = remember { viewModel.uiState }

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

        // Отображаем содержимое в зависимости от состояния
        when (val state = uiState.value) {
            is DoctorListUiState.Loading -> {
                // Состояние загрузки: показываем спиннер
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is DoctorListUiState.Success -> {
                // Состояние успеха: показываем список врачей
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.doctors) { doctor ->
                        DoctorCard(doctor = doctor)
                    }
                }
            }

            is DoctorListUiState.Error -> {
                // Состояние ошибки: показываем сообщение об ошибке
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        // Кнопка для повторной попытки загрузки
                        Button(onClick = { viewModel.loadDoctors() }) {
                            Text(text = "Қайта өндіру")
                        }
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
private fun DoctorCard(doctor: DoctorResponse) {
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
                onClick = { /* TODO: Реализовать переход на экран выбора даты и времени */ },
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
