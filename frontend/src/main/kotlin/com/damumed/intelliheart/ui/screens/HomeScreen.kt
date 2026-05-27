package com.damumed.intelliheart.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.damumed.intelliheart.voice.VoiceAssistantManager

/**
 * Главный экран приложения (Басты бет)
 * Отображает приветственное сообщение и кнопку для вызова врача на дом
 * Включает FloatingActionButton с микрофоном для голосового помощника
 */
@Composable
fun HomeScreenMain(
    modifier: Modifier = Modifier,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnalyses: () -> Unit = {}
) {
    // Получаем контекст приложения
    val context = LocalContext.current

    // Состояние для отслеживания распознанного текста
    val recognizedText = remember { mutableStateOf("") }

    // Создаем менеджер голосового помощника
    val voiceManager = remember { VoiceAssistantManager(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening()
        } else {
            Toast.makeText(context, "Микрофонға рұқсат берілмеді", Toast.LENGTH_SHORT).show()
        }
    }

    // Устанавливаем callback для результатов распознавания
    LaunchedEffect(context) {
        voiceManager.setOnResultCallback { text ->
            recognizedText.value = text
            // Показываем результат в Toast
            Toast.makeText(context, "Танылған мәтін: $text", Toast.LENGTH_SHORT).show()
        }

        voiceManager.setOnErrorCallback { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
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
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Основное сообщение
            Text(
                text = "Сіздің денсаулығыңыз – біздің басты байлығымыз.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Кнопка для вызова врача на дом
            Button(
                onClick = onNavigateToCallDoctor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = "Дәрігерді үйге шақыру",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Кнопка для записи к врачу в клинику
            FilledTonalButton(
                onClick = onNavigateToAppointments,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = "Дәрігерге жазылу",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Кнопка для просмотра медицинской карты
            FilledTonalButton(
                onClick = onNavigateToRecords,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = "Медициналық картамды қарау",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Кнопка для анализов
            FilledTonalButton(
                onClick = onNavigateToAnalyses,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = "Анализ нәтижелерін қарау",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Кнопка для профиля
            OutlinedButton(
                onClick = onNavigateToProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = "Менің профилім",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Текст распознанной речи (если есть)
            if (recognizedText.value.isNotEmpty()) {
                Text(
                    text = "Танылған мәтін: ${recognizedText.value}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // FloatingActionButton с микрофоном в правом нижнем углу
        FloatingActionButton(
            onClick = {
                val hasAudioPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                if (hasAudioPermission) {
                    voiceManager.startListening()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
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
