package dev.waldq.rostelekom.ui.speedtest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fdossena.speedtest.core.Speedtest
import dev.waldq.rostelekom.engine.TestEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpeedtestViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<SpeedtestUiState>(SpeedtestUiState())
    val uiState = _uiState.asStateFlow()

    private val engine = TestEngine(application)

    //written by the UI thread (stop) and the engine's worker/stream threads
    @Volatile
    private var aborted = false
    @Volatile
    private var failed = false

    init {
        discoverServers()
    }

    fun startOrStop() {
        if (isRunning()) stop() else start()
    }

    private fun discoverServers() {
        _uiState.update { it.copy(isDiscovering = true) }
        viewModelScope.launch {
            try {
                val discovery = engine.discover(emptyList())

                _uiState.update {
                    it.copy(
                        servers = discovery.servers,
                        selectedServer = discovery.selected
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        phase = Phase.ERROR,
                        error = e.message
                    )
                }
            }
        }
        _uiState.update { it.copy(isDiscovering = false) }
    }

    private fun isRunning(): Boolean =
        _uiState.value.phase in setOf(Phase.PING, Phase.DOWNLOAD, Phase.UPLOAD)

    private fun start() {
        val current = _uiState.value
        val server = current.selectedServer ?: return
        aborted = false
        failed = false
        _uiState.update {
            it.copy(
                phase = Phase.PING,
                currentSpeed = 0.0,
                download = -1.0, upload = -1.0, ping = -1.0, jitter = -1.0, loss = -1.0,
                downloadSamples = emptyList(), uploadSamples = emptyList(),
                ipInfo = null, shareUrl = null, error = null
            )
        }
        try {
            engine.prepare(current.servers, server, telemetryEnabled = true)
            engine.start(object : Speedtest.SpeedtestHandler() {
                override fun onDownloadUpdate(dl: Double, progress: Double) {
                    if (aborted || failed) return

                    _uiState.update {
                        it.copy(
                            phase = Phase.DOWNLOAD,
                            currentSpeed = dl,
                            download = dl,
                            downloadSamples = if (progress > 0) it.downloadSamples + dl else it.downloadSamples
                        )
                    }
                }

                override fun onUploadUpdate(ul: Double, progress: Double) {
                    if (aborted || failed) return

                    _uiState.update {
                        it.copy(
                            phase = Phase.UPLOAD,
                            currentSpeed = ul,
                            upload = ul,
                            uploadSamples = if (progress > 0) it.uploadSamples + ul else it.uploadSamples
                        )
                    }
                }

                override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                    if (aborted || failed) return

                    _uiState.update { it.copy(phase = Phase.PING, ping = ping, jitter = jitter) }
                }

                override fun onLossUpdate(loss: Double) {
                    if (aborted || failed) return

                    _uiState.update { it.copy(loss = loss) }
                }

                override fun onIPInfoUpdate(ipInfo: String?) {
                    if (aborted || failed) return

                    _uiState.update { it.copy(ipInfo = ipInfo) }
                }

                override fun onTestIDReceived(id: String?, shareURL: String?) {
                    if (aborted || failed) return

                    _uiState.update { it.copy(shareUrl = shareURL) }
                }

                override fun onEnd() {
                    if (failed) return
                    val finished = _uiState.value
                    if (aborted || finished.download < 0) {
                        resetToIdle()
                        return
                    }
                    finish()
                }

                override fun onCriticalFailure(err: String?) {
                    failed = true
                    _uiState.update { it.copy(phase = Phase.ERROR, error = err, currentSpeed = 0.0) }
                }
            })
        } catch (_: Exception) {
            resetToIdle()
        }
    }

    private fun stop() {
        aborted = true
        engine.abort()
        _uiState.update { it.copy(phase = Phase.IDLE) }
    }

    private fun finish() {
        _uiState.update {
            it.copy(
                phase = Phase.IDLE,
                currentSpeed = 0.0
            )
        }
    }

    private fun resetToIdle() {
        _uiState.update {
            it.copy(
                phase = Phase.IDLE,
                currentSpeed = 0.0,
                download = -1.0, upload = -1.0, ping = -1.0, jitter = -1.0, loss = -1.0,
                downloadSamples = emptyList(), uploadSamples = emptyList(),
                ipInfo = null, shareUrl = null, error = null
            )
        }
    }

    override fun onCleared() {
        engine.abort()
        super.onCleared()
    }
}