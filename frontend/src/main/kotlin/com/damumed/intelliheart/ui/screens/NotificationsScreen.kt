package com.damumed.intelliheart.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.NotificationResponse
import com.damumed.intelliheart.notifications.NotificationHelper
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.NotificationsUiState
import com.damumed.intelliheart.viewmodel.NotificationsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotificationsScreen(
    session: AuthSession?
) {
    val viewModel: NotificationsViewModel = viewModel()
    val uiState = viewModel.uiState.value
    val context = LocalContext.current

    var pendingNotification by remember { mutableStateOf<NotificationResponse?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingNotification?.let {
                NotificationHelper.showNotification(context, it.title, it.message)
            }
        } else {
            Toast.makeText(context, "Хабарландыруларға рұқсат берілмеді", Toast.LENGTH_SHORT).show()
        }
        pendingNotification = null
    }

    LaunchedEffect(session?.patientId, session?.userId) {
        val targetId = session?.patientId ?: session?.userId
        viewModel.loadNotifications(targetId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Хабарландырулар",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Button(
            onClick = {
                viewModel.createNotification(
                    userId = session?.patientId ?: session?.userId,
                    title = "IntelliHeart",
                    message = "Тесттік хабарлама: сіздің жазбаңыз сәтті сақталды."
                ) { notification ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        NotificationHelper.showNotification(context, notification.title, notification.message)
                    } else {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            NotificationHelper.showNotification(context, notification.title, notification.message)
                        } else {
                            pendingNotification = notification
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Тесттік хабарлама жіберу")
        }

        when (uiState) {
            is NotificationsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is NotificationsUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is NotificationsUiState.Success -> {
                if (uiState.notifications.isEmpty()) {
                    Text(
                        text = "Әзірге хабарламалар жоқ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notifications) { notification ->
                            NotificationCard(notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = notification.message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatTimestamp(notification.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
