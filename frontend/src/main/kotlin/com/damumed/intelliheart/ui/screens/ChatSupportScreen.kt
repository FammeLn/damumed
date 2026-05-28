package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.viewmodel.AssistantMessage
import com.damumed.intelliheart.viewmodel.ChatSupportViewModel

/**
 * Экран чата поддержки с AI-ботом
 * Пользователь отправляет сообщение, бот отвечает через /api/assistant/query
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSupportScreen(
    viewModel: ChatSupportViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToCallDoctor: () -> Unit = {},
    onNavigateToRecords: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state = viewModel.state.value
    var inputText by remember { mutableStateOf("") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Заголовок
        TopAppBar(
            title = {
                Text(
                    text = "Қолдау",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Артқа"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // История сообщений
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Сұрағыңызды жазыңыз. Көмекшімізге сұрау жіберіңіз.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Ошибка
        if (state.error != null) {
            Text(
                text = "Қате: ${state.error}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
        }

        // Ввод сообщения
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Хабарлама жазыңыз...")
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = false,
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (inputText.trim().isNotEmpty()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(24.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Жіберу",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Компонент для отображения сообщения в чате
 */
@Composable
private fun ChatMessageBubble(message: AssistantMessage) {
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
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = if (isUserMessage)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}
