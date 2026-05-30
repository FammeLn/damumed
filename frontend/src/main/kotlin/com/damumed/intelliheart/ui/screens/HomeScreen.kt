package com.damumed.intelliheart.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.damumed.intelliheart.settings.AppSettings
import com.damumed.intelliheart.ui.theme.IntelliHeartColors
import com.damumed.intelliheart.voice.VoiceAssistantManager

/**
 * Главный экран приложения (Басты бет) - Премиум Дизайн
 */
@Composable
fun HomeScreenMain(
    modifier: Modifier = Modifier,
    language: String = AppSettings.LANG_KZ,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnalyses: () -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) {
    val context = LocalContext.current
    val recognizedText = remember { mutableStateOf("") }
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

    LaunchedEffect(context) {
        voiceManager.setOnResultCallback { text ->
            recognizedText.value = text
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

    val infiniteTransition = rememberInfiniteTransition(label = "fab")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Заголовок профиля и приветствие
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppSettings.LANG_KZ) "Сәлеметсіз бе! 👋" else "Здравствуйте! 👋",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = if (language == AppSettings.LANG_KZ)
                            "Бүгінгі денсаулығыңыз қалай?"
                        else
                            "Как ваше здоровье сегодня?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(IntelliHeartColors.PrimaryGradient)
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = if (language == AppSettings.LANG_KZ) "Профиль" else "Профиль",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }


            // Карточка перехода в чат с AI Помощником (Удобный доступ в один клик)
            Card(
                onClick = onNavigateToChat,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IntelliHeartColors.PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (language == AppSettings.LANG_KZ) "Damumed AI Көмекші" else "Damumed AI Помощник",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (language == AppSettings.LANG_KZ)
                                    "Денсаулық бойынша сұрақтар қою"
                                else
                                    "Задать вопрос о здоровье",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Быстрые действия (2x2 Grid)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (language == AppSettings.LANG_KZ) "Жылдам әрекеттер" else "Быстрые действия",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ActionCard(
                            title = if (language == AppSettings.LANG_KZ) "Дәрігерге жазылу" else "Запись к врачу",
                            subtitle = if (language == AppSettings.LANG_KZ) "Клиникаға тіркелу" else "Записаться в клинику",
                            icon = Icons.Default.Event,
                            iconBg = MaterialTheme.colorScheme.primaryContainer,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = onNavigateToAppointments
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ActionCard(
                            title = if (language == AppSettings.LANG_KZ) "Үйге шақыру" else "Вызов на дом",
                            subtitle = if (language == AppSettings.LANG_KZ) "Дәрігерді үйге шақыру" else "Вызвать врача домой",
                            icon = Icons.Default.LocalHospital,
                            iconBg = MaterialTheme.colorScheme.secondaryContainer,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            onClick = onNavigateToCallDoctor
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ActionCard(
                            title = if (language == AppSettings.LANG_KZ) "Медициналық карта" else "Медкарта",
                            subtitle = if (language == AppSettings.LANG_KZ) "Жазбаларды қарау" else "Просмотр записей",
                            icon = Icons.Default.Assignment,
                            iconBg = MaterialTheme.colorScheme.tertiaryContainer,
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            onClick = onNavigateToRecords
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ActionCard(
                            title = if (language == AppSettings.LANG_KZ) "Анализдер" else "Анализы",
                            subtitle = if (language == AppSettings.LANG_KZ) "Нәтижелерді қарау" else "Просмотр результатов",
                            icon = Icons.Default.Science,
                            iconBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = onNavigateToAnalyses
                        )
                    }
                }
            }

            if (recognizedText.value.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "Танылған мәтін: ${recognizedText.value}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // FAB с микрофоном и пульсирующим фоном
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp * scale)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            )
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
                modifier = Modifier.align(Alignment.Center),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Дауыс көмекшісі"
                )
            }
        }
    }
}




/**
 * Красивая современная карточка действия
 */
@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
