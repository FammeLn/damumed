package com.damumed.intelliheart.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.voice.TextToSpeechManager
import com.damumed.intelliheart.voice.VoiceAssistantManager
import com.damumed.intelliheart.viewmodel.AssistantMessage
import com.damumed.intelliheart.viewmodel.HomeScreenViewModel
import androidx.compose.ui.platform.LocalContext

/**
 * Главный экран приложения (Басты бет)
 * Полная интеграция микрофона, голосового помощника и озвучивания
 */
@Composable
fun HomeScreenIntegrated(
    modifier: Modifier = Modifier,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnalyses: () -> Unit = {}
) {
    // Получаем контекст приложения
    val context = LocalContext.current

    // Получаем ViewModel для управления состоянием экрана
    val viewModel: HomeScreenViewModel = viewModel()
    val screenState = viewModel.screenState

    // Создаем менеджер голосового помощника (микрофон)
    val voiceManager = remember { VoiceAssistantManager(context) }

    // Создаем менеджер для озвучивания ответов
    val ttsManager = remember { TextToSpeechManager(context) }

    // Устанавливаем callback для микрофона
    LaunchedEffect(voiceManager) {
        voiceManager.setOnResultCallback { text ->
            // Обрабатываем распознанный текст
            viewModel.processRecognizedText(text)
            Toast.makeText(context, "Танылған мәтін: $text", Toast.LENGTH_SHORT).show()
        }

        voiceManager.setOnErrorCallback { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    // Озвучиваем ответ помощника, если он пришел
    val currentState = screenState.value
    if (currentState.messages.isNotEmpty()) {
        val lastMessage = currentState.messages.lastOrNull()
        if (lastMessage?.sender == "ASSISTANT" && ttsManager.isReady()) {
            // Озвучиваем последний ответ помощника
            ttsManager.speak(lastMessage.text) {
                // Проверяем, нужна ли навигация на основе рекомендации помощника
                when (currentState.lastAction) {
                    "NAVIGATE_TO_APPOINTMENT" -> {
                        onNavigateToAppointments()
                    }
                    "CALL_HOME_DOCTOR" -> {
                        onNavigateToCallDoctor()
                    }
                    "NAVIGATE_TO_RECORDS" -> {
                        onNavigateToRecords()
                    }
                    "NAVIGATE_TO_PROFILE" -> {
                        onNavigateToProfile()
                    }
                    // Если action не распознан, ничего не делаем
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Text(
                text = "Қайырлы күн!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Сіздің денсаулығыңыз – біздің басты байлығымыз.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            // Быстрые действия
            Button(
                onClick = { /* TODO: Реализовать логику вызова врача */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Дәрігерді үйге шақыру",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            FilledTonalButton(
                onClick = { onNavigateToAppointments() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Дәрігерге жазылу",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            FilledTonalButton(
                onClick = { onNavigateToRecords() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Медициналық картамды қарау",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
            }
            
            FilledTonalButton(
                onClick = { onNavigateToAnalyses() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Анализ нәтижелерін қарау",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = { onNavigateToProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Жеке кабинет",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            // История сообщений - диалог с помощником
            if (currentState.messages.isNotEmpty()) {
                Text(
                    text = "Диалог",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentState.messages) { message ->
                        MessageBubble(message = message)
                    }
                }
            } else {
                // Пусто - покажем подсказку
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Микрофон батырмасын басыңыз және сұрағыңызды айтыңыз",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }

            // Обработка ошибок
            if (currentState.error != null) {
                Text(
                    text = "Қате: ${currentState.error}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Состояние загрузки
            if (currentState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
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
                contentDescription = "Дауыс көмекшісі"
            )
        }
    }
}

/**
 * Компонент для отображения сообщения в диалоге
 */
@Composable
private fun MessageBubble(message: AssistantMessage) {
    val isUserMessage = message.sender == "USER"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isUserMessage) 24.dp else 0.dp),
        contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUserMessage)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = message.text,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = if (isUserMessage)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}
