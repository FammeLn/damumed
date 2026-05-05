package com.damumed.intelliheart.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damumed.intelliheart.voice.VoiceAssistantManager

/**
 * Главный экран приложения (Басты бет)
 * Отображает приветственное сообщение и кнопку для вызова врача на дом
 * Включает FloatingActionButton с микрофоном для голосового помощника
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    // Получаем контекст приложения
    val context = LocalContext.current

    // Состояние для отслеживания распознанного текста
    val recognizedText = remember { mutableStateOf("") }

    // Создаем менеджер голосового помощника
    val voiceManager = remember { VoiceAssistantManager(context) }

    // Устанавливаем callback для результатов распознавания
    remember {
        voiceManager.setOnResultCallback { text ->
            recognizedText.value = text
            // Показываем результат в Toast
            Toast.makeText(context, "Танығаны: $text", Toast.LENGTH_SHORT).show()
        }

        voiceManager.setOnErrorCallback { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
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
                onClick = onNavigateToCallDoctor,
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
                onClick = onNavigateToAppointments,
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
                onClick = onNavigateToRecords,
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

            // Кнопка для профиля
            Button(
                onClick = onNavigateToProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Менің профилім",
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Текст распознанной речи (если есть)
            if (recognizedText.value.isNotEmpty()) {
                Text(
                    text = "Танығаны: ${recognizedText.value}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // FloatingActionButton с микрофоном в правом нижнем углу
        FloatingActionButton(
            onClick = {
                // Запускаем распознавание речи
                voiceManager.startListening()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Голос көмегі"
            )
        }
    }
