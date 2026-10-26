package com.tecsup.vigiaire.data.network

import com.tecsup.vigiaire.data.model.EstadoSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject
import java.util.UUID

/** Cliente MQTT y sesión de lecturas en vivo de Wokwi. */
object MqttVigiAireClient {
    const val BROKER = "tcp://broker.hivemq.com:1883"
    const val TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry"
    const val LIVE_TIMEOUT_MS = 10_000L
    const val MAX_SESSION_READINGS = 2_000

    private val _state = MutableStateFlow(MqttConnectionState.DISCONNECTED)
    val state: StateFlow<MqttConnectionState> = _state.asStateFlow()

    private val _latestReading = MutableStateFlow<MqttGasReading?>(null)
    val latestReading: StateFlow<MqttGasReading?> = _latestReading.asStateFlow()

    private val _sessionReadings = MutableStateFlow<List<MqttGasReading>>(emptyList())
    val sessionReadings: StateFlow<List<MqttGasReading>> = _sessionReadings.asStateFlow()

    private val _sessionNumber = MutableStateFlow(1)
    val sessionNumber: StateFlow<Int> = _sessionNumber.asStateFlow()

    private val _sessionStartedAt = MutableStateFlow<Long?>(null)
    val sessionStartedAt: StateFlow<Long?> = _sessionStartedAt.asStateFlow()

    private val _sessionReadingCount = MutableStateFlow(0L)
    val sessionReadingCount: StateFlow<Long> = _sessionReadingCount.asStateFlow()

    private val _sessionMinimum = MutableStateFlow<MqttGasReading?>(null)
    val sessionMinimum: StateFlow<MqttGasReading?> = _sessionMinimum.asStateFlow()

    private val _sessionMaximum = MutableStateFlow<MqttGasReading?>(null)
    val sessionMaximum: StateFlow<MqttGasReading?> = _sessionMaximum.asStateFlow()

    private val _sessionAverage = MutableStateFlow<Int?>(null)
    val sessionAverage: StateFlow<Int?> = _sessionAverage.asStateFlow()
    private var sessionSignalSum = 0L

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    @Volatile
    private var client: MqttAsyncClient? = null
    private var lastUptimeMs: Long? = null
    private var lastSequence: Long? = null

    @Synchronized
    fun connect() {
        if (_state.value == MqttConnectionState.CONNECTING || client?.isConnected == true) return

        try {
            _error.value = null
            _state.value = MqttConnectionState.CONNECTING

            val mqttClient = client?.takeIf { !it.isConnected } ?: MqttAsyncClient(
                BROKER,
                "VigiAire-Android-${UUID.randomUUID()}",
                MemoryPersistence()
            ).also { client = it }

            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    _state.value = MqttConnectionState.CONNECTED
                    subscribe(mqttClient)
                }

                override fun connectionLost(cause: Throwable?) {
                    _state.value = MqttConnectionState.DISCONNECTED
                    _error.value = cause?.message ?: "Se perdió la conexión con el broker."
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != TOPIC || message == null) return
                    runCatching {
                        val json = JSONObject(String(message.payload, Charsets.UTF_8))
                        require(json.optString("device") == "VigiAire-ESP32")
                        require(json.optString("sensor") == "MQ-2")
                        val rawStatus = json.getString("status").uppercase()
                        require(EstadoSensor.from(rawStatus) != EstadoSensor.DESCONOCIDO)
                        val gasAdc = json.getInt("gasAdc")
                        require(gasAdc in 0..4095) { "La lectura ADC del ESP32 debe estar entre 0 y 4095." }
                        MqttGasReading(
                            gasAdc = gasAdc,
                            status = rawStatus,
                            sequence = json.optLong("seq", 0L),
                            uptimeMs = json.optLong("uptimeMs", 0L).coerceAtLeast(0L),
                            receivedAt = System.currentTimeMillis()
                        )
                    }.onSuccess(::recordReading)
                        .onFailure { _error.value = "Se ignoró un mensaje ajeno o con formato inesperado." }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
            })

            val options = MqttConnectOptions().apply {
                isCleanSession = true
                isAutomaticReconnect = true
                connectionTimeout = 10
                keepAliveInterval = 30
            }

            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?) {
                    _state.value = MqttConnectionState.CONNECTED
                }

                override fun onFailure(
                    asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?,
                    exception: Throwable?
                ) {
                    _state.value = MqttConnectionState.ERROR
                    _error.value = exception?.message ?: "No se pudo conectar al broker MQTT."
                }
            })
        } catch (exception: Exception) {
            _state.value = MqttConnectionState.ERROR
            _error.value = exception.message ?: "Error al iniciar MQTT."
        }
    }

    @Synchronized
    fun disconnect() {
        val activeClient = client
        client = null
        _state.value = MqttConnectionState.DISCONNECTED
        _error.value = null
        if (activeClient != null) {
            runCatching {
                if (activeClient.isConnected) activeClient.disconnect()
                activeClient.close()
            }
        }
    }

    private fun recordReading(reading: MqttGasReading) {
        val rebooted = lastUptimeMs != null &&
            (reading.uptimeMs < lastUptimeMs!! || reading.sequence < (lastSequence ?: 0L))

        if (rebooted) {
            _sessionNumber.value += 1
            _sessionReadings.value = emptyList()
            _sessionStartedAt.value = reading.receivedAt
            _sessionReadingCount.value = 0L
            _sessionMinimum.value = null
            _sessionMaximum.value = null
            _sessionAverage.value = null
            sessionSignalSum = 0L
        } else if (_sessionStartedAt.value == null) {
            _sessionStartedAt.value = reading.receivedAt
        }

        lastUptimeMs = reading.uptimeMs
        lastSequence = reading.sequence
        _latestReading.value = reading
        _sessionReadings.value = (_sessionReadings.value + reading).takeLast(MAX_SESSION_READINGS)
        _sessionReadingCount.value += 1
        if (_sessionMinimum.value == null || reading.gasAdc < _sessionMinimum.value!!.gasAdc) _sessionMinimum.value = reading
        if (_sessionMaximum.value == null || reading.gasAdc > _sessionMaximum.value!!.gasAdc) _sessionMaximum.value = reading
        sessionSignalSum += reading.gasAdc
        _sessionAverage.value = (sessionSignalSum / _sessionReadingCount.value).toInt()
        _error.value = null
    }

    private fun subscribe(mqttClient: MqttAsyncClient) {
        if (!mqttClient.isConnected) return
        mqttClient.subscribe(TOPIC, 0, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?) {
                _error.value = null
            }

            override fun onFailure(
                asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?,
                exception: Throwable?
            ) {
                _error.value = exception?.message ?: "No se pudo escuchar el tema MQTT."
            }
        })
    }
}

enum class MqttConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class MqttGasReading(
    val gasAdc: Int,
    val status: String,
    val sequence: Long,
    val uptimeMs: Long,
    val receivedAt: Long
) {
    val estado: EstadoSensor get() = EstadoSensor.from(status)
}
