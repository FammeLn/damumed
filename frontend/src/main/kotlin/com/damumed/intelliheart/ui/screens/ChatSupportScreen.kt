package com.damumed.intelliheart.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.ui.theme.IntelliHeartColors
import com.damumed.intelliheart.viewmodel.AssistantMessage
import com.damumed.intelliheart.viewmodel.ChatSupportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран чата поддержки с AI-ботом (Премиум Дизайн)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSupportScreen(
    session: AuthSession?,
    viewModel: ChatSupportViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state = viewModel.state.value
    var inputText by remember { mutableStateOf("") }
    val patientId = session?.patientId ?: 0L
    val listState = rememberLazyListState()

    LaunchedEffect(patientId) {
        if (patientId > 0) {
            viewModel.loadHistory(patientId)
        }
    }

    // Авто-скролл к последнему сообщению при добавлении новых
    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size)
        }
    }

    LaunchedEffect(state.lastAction) {
        if (state.lastAction != "NONE") {
            when (state.lastAction) {
                "NAVIGATE_TO_APPOINTMENT" -> {
                    onNavigateToAppointments()
                    viewModel.clearAction()
                }
                "CALL_HOME_DOCTOR" -> {
                    onNavigateToCallDoctor()
                    viewModel.clearAction()
                }
                "NAVIGATE_TO_RECORDS" -> {
                    onNavigateToRecords()
                    viewModel.clearAction()
                }
                "NAVIGATE_TO_PROFILE" -> {
                    onNavigateToProfile()
                    viewModel.clearAction()
                }
            }
        }
    }

    val suggestionChips = listOf(
        "👋 Сәлеметсіз бе" to "Сәлеметсіз бе",
        "🕒 Жұмыс уақыты" to "Клиниканың жұмыс уақыты қандай?",
        "📞 Байланыс" to "Байланыс телефондары қандай?",
        "🤒 Ауырып қалдым" to "Температура көтерілсе не істеу керек?"
    )

    // Анимация пульсации статуса онлайн
    val onlinePulseTransition = rememberInfiniteTransition(label = "onlinePulse")
    val onlineAlpha by onlinePulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onlineAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Кастомный современный заголовок
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Артқа",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Аватар бота с градиентом
                Box(
                    modifier = Modifier
                        .size(42.dp)
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

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Damumed Көмекші",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp * onlineAlpha)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.4f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                        Text(
                            text = "Онлайн",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // История сообщений
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.messages.isEmpty() && !state.isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Text(
                            text = "Сәлеметсіз бе! Мен сіздің виртуалды медициналық көмекшіңізбін.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Клиника жұмысы, байланыс телефондары туралы сұрай аласыз немесе дәрігерді үйге шақыруға көмектесе аламын.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.messages) { message ->
                    ChatMessageBubble(message = message)
                }
            }

            if (state.isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Жауап дайындауда...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Подсказки-чипсы (Suggestion chips) над полем ввода с горизонтальной прокруткой
        if (patientId > 0L && !state.isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                suggestionChips.forEach { (label, payload) ->
                    QuickSuggestionChip(label = label) {
                        viewModel.sendMessage(patientId, payload)
                    }
                }
            }
        }

        // Сообщение об ошибке
        if (state.error != null) {
            Snackbar(
                modifier = Modifier.padding(12.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Қате: ${state.error}", fontSize = 13.sp)
            }
        }

        // Ввод сообщения
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(12.dp)
            ) {
                if (patientId == 0L) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Чатты пайдалану үшін алдымен жеке кабинетте профильді толтырыңыз.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text("Хабарлама жазыңыз...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            ),
                            maxLines = 4
                        )

                        val sendBtnModifier = if (inputText.trim().isNotEmpty()) {
                            Modifier.background(IntelliHeartColors.PrimaryGradient)
                        } else {
                            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .then(sendBtnModifier)
                                .clickable(enabled = inputText.trim().isNotEmpty()) {
                                    if (inputText.trim().isNotEmpty()) {
                                        viewModel.sendMessage(patientId, inputText)
                                        inputText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Жіберу",
                                tint = if (inputText.trim().isNotEmpty())
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Компонент для отображения сообщения в чате с красивым градиентом
 */
@Composable
private fun ChatMessageBubble(message: AssistantMessage) {
    val isUserMessage = message.sender == "USER"
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUserMessage) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start
        ) {
            Surface(
                modifier = Modifier.widthIn(max = 270.dp),
                shape = if (isUserMessage) {
                    RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp)
                } else {
                    RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp)
                },
                color = if (isUserMessage) Color.Unspecified else MaterialTheme.colorScheme.surface,
                border = if (isUserMessage) null else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                shadowElevation = 1.dp
            ) {
                // Красивый градиент для сообщений пользователя
                val bubbleModifier = if (isUserMessage) {
                    Modifier.background(IntelliHeartColors.PrimaryGradient)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surface)
                }

                Box(
                    modifier = bubbleModifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = if (isUserMessage)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formattedTime,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (isUserMessage) {
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

/**
 * Премиальный пилюлеобразный чип для подсказок
 */
@Composable
private fun QuickSuggestionChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
