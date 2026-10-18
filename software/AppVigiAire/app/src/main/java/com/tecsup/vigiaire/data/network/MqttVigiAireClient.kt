package com.tecsup.vigiaire.data.network

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

/** Cliente MQTT de demostración: recibe la señal relativa que publica Wokwi. */
object MqttVigiAireClient {
    const val BROKER = "tcp://broker.hivemq.com:1883"
    const val TOPIC = "vigiaire/c24d/grupo4/9f3c7d8b/telemetry"

    private val _state = MutableStateFlow(MqttConnectionState.DISCONNECTED)
    val state: StateFlow<MqttConnectionState> = _state.asStateFlow()

    private val _latestReading = MutableStateFlow<MqttGasReading?>(null)
    val latestReading: StateFlow<MqttGasReading?> = _latestReading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    @Volatile
    private var client: MqttAsyncClient? = null

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
                        MqttGasReading(
                            gasAdc = json.getInt("gasAdc").coerceIn(0, 4095),
                            status = json.getString("status"),
                            sequence = json.optLong("seq"),
                            receivedAt = System.currentTimeMillis()
                        )
                    }.onSuccess { _latestReading.value = it }
                        .onFailure { _error.value = "Llegó un mensaje con formato inesperado." }
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
    val receivedAt: Long
)
