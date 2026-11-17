package dev.waldq.rostelekom.ui.speedtest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.R
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme

@Composable
fun SpeedtestRoute(
    onBack: () -> Unit,
    viewModel: SpeedtestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SpeedtestScreen(
        uiState = uiState,
        startOrStop = viewModel::startOrStop,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedtestScreen(
    uiState: SpeedtestUiState,
    modifier: Modifier = Modifier,
    startOrStop: () -> Unit,
    onBack: () -> Unit,
) {
    val running = uiState.phase in setOf(
        Phase.PING,
        Phase.DOWNLOAD,
        Phase.UPLOAD
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Тест скорости",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(10.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = uiState.selectedServer?.name ?: "Поиск сервера...",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    overlineContent = {
                        Text(
                            text = "СЕРВЕР ТЕСТИРОВАНИЯ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_globe),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                )
            }

            SpeedIndicator(
                modifier = Modifier.fillMaxWidth(0.75f),
                currentSpeed = uiState.currentSpeed,
                phase = uiState.phase,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Загрузка",
                    value = uiState.download,
                    unit = "Мбит/с",
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Отдача",
                    value = uiState.upload,
                    unit = "Мбит/с",
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SmallMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Ping",
                    value = uiState.ping,
                    unit = "мс",
                )
                SmallMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Jitter",
                    value = uiState.jitter,
                    unit = "мс",
                )
                SmallMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Потери",
                    value = uiState.loss,
                    unit = "%",
                )
            }

            if (uiState.phase == Phase.ERROR) {
                Text(
                    text = uiState.error ?: "Неизвестная ошибка",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = startOrStop,
                enabled = uiState.selectedServer != null || running,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(if (running) "Остановить" else "Начать тест")
            }
        }
    }
}

@Composable
private fun SpeedIndicator(
    modifier: Modifier = Modifier,
    currentSpeed: Double,
    phase: Phase,
) {
    val blankColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    val filledColor = MaterialTheme.colorScheme.primary

    val maxSpeed = 1024.0
    val progress = (currentSpeed / maxSpeed).coerceIn(0.0, 1.0)

    val speedText = if (phase in setOf(
            Phase.PING,
            Phase.DOWNLOAD,
            Phase.UPLOAD
        )
    ) {
        "%.1f".format(currentSpeed)
    } else {
        "—"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            ) {
                val strokeWidth = 14.dp.toPx()
                val inset = strokeWidth / 2

                val arcSize = Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth,
                )

                val arcTopLeft = Offset(inset, inset)

                drawArc(
                    startAngle = 30f,
                    sweepAngle = -240f,
                    useCenter = false,
                    color = blankColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                    ),
                    topLeft = arcTopLeft,
                    size = arcSize,
                )

                drawArc(
                    startAngle = -210f,
                    sweepAngle = 240f * progress.toFloat(),
                    useCenter = false,
                    color = filledColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                    ),
                    topLeft = arcTopLeft,
                    size = arcSize,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = speedText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
                Text(
                    text = "Мбит/с",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = phaseLabel(phase),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Text(
            text = if (phase == Phase.IDLE) {
                "Нажмите кнопку, чтобы начать"
            } else {
                "Текущая скорость соединения"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun phaseLabel(phase: Phase): String = when (phase) {
    Phase.IDLE -> "ГОТОВ К ТЕСТУ"
    Phase.PING -> "ИЗМЕРЕНИЕ PING"
    Phase.DOWNLOAD -> "ЗАГРУЗКА"
    Phase.UPLOAD -> "ОТДАЧА"
    Phase.ERROR -> "ОШИБКА"
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    unit: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = if (value < 0) "—" else "%.1f".format(value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SmallMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    unit: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = if (value < 0) "—" else "%.1f".format(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun SpeedtestScreenPreview() {
    RostelekomitandroidTheme {
        SpeedtestScreen(
            uiState = SpeedtestUiState(),
            startOrStop = {},
            onBack = {},
        )
    }
}
