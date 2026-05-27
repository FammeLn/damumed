package com.damumed.intelliheart.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damumed.intelliheart.network.dto.AnalysisResponse
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.viewmodel.AnalysesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysesScreen(
    session: AuthSession?,
    onBack: () -> Unit
) {
    val viewModel: AnalysesViewModel = viewModel()
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Загружаем анализы текущего пациента, если он авторизован
        val patientId = session?.patientId ?: 1L // fallback 1L для теста
        viewModel.loadAnalyses(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Анализ нәтижелері") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Артқа")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = "Қате: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.analyses.isEmpty()) {
                Text(
                    text = "Анализдер табылмады",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.analyses) { analysis ->
                        AnalysisCard(analysis) { id ->
                            // Открываем PDF в браузере
                            // Предполагается, что бекенд запущен на localhost:8080.
                            // Для реального девайса нужен IP компьютера, например 192.168.x.x
                            val baseUrl = com.damumed.intelliheart.network.RetrofitClient.getBaseUrl().removeSuffix("/")
                            val pdfUrl = "$baseUrl/api/analyses/$id/download"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisCard(analysis: AnalysisResponse, onDownloadClick: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = analysis.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${analysis.date} • ${analysis.clinic}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Статус: ${analysis.status}",
                    fontWeight = FontWeight.SemiBold,
                    color = if (analysis.status == "ГОТОВ") 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                
                if (analysis.hasPdf) {
                    OutlinedButton(onClick = { onDownloadClick(analysis.id) }) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF жүктеу")
                    }
                }
            }
        }
    }
}
