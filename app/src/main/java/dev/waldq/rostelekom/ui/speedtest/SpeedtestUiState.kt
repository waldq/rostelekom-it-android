package dev.waldq.rostelekom.ui.speedtest

import com.fdossena.speedtest.core.serverSelector.TestPoint


enum class Phase { IDLE, PING, DOWNLOAD, UPLOAD, ERROR }
data class SpeedtestUiState(
    val phase: Phase = Phase.IDLE,
    val isDiscovering: Boolean = false,
    val servers: List<TestPoint> = emptyList(),
    val selectedServer: TestPoint? = null,
    val currentSpeed: Double = 0.0,
    val download: Double = -1.0,
    val upload: Double = -1.0,
    val ping: Double = -1.0,
    val jitter: Double = -1.0,
    val loss: Double = -1.0,
    val downloadSamples: List<Double> = emptyList(),
    val uploadSamples: List<Double> = emptyList(),
    val ipInfo: String? = null,
    val shareUrl: String? = null,
    val error: String? = null,
)
