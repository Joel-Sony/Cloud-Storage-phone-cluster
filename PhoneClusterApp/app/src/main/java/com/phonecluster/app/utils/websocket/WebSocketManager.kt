package com.phonecluster.app.utils.websocket

import android.content.Context
import com.phonecluster.app.storage.PreferencesManager
import com.phonecluster.app.utils.DeviceInfoProvider
import org.json.JSONObject

object WebSocketManager {

    private var client: DeviceWebSocketClient? = null
    private var isConnected = false

    fun connect(context: Context, serverIp: String) {
        if (isConnected) return

        val deviceId = PreferencesManager.getDeviceId(context) ?: return

        val registerPayload = JSONObject().apply {
            put("type", "register") // not exactly register but in server its defined this way
            put("device_id", deviceId)
            put("fingerprint", DeviceInfoProvider.getDeviceFingerprint(context))
            put("device_name", DeviceInfoProvider.getDeviceName())
            put("storage_capacity", DeviceInfoProvider.getTotalStorageBytes())
            put("available_storage", DeviceInfoProvider.getAvailableStorageBytes())
        }

        val wsUrl = "ws://$serverIp:8000/ws/device"

        client = DeviceWebSocketClient(
            serverWsUrl = wsUrl,
            onMessageReceived = { msg ->
                handleServerMessage(msg)
            },
            onDisconnected = {
                isConnected = false
            }
        )

        client?.connect(registerPayload)
        isConnected = true
    }

    fun disconnect() {
        client?.disconnect()
        client = null
        isConnected = false
    }

    private fun handleServerMessage(msg: JSONObject) {
        when (msg.optString("type")) {
            "ready" -> {
                // server acknowledged WS connection
            }

            "STORE_CHUNK" -> {
                // future: handle store chunk command
            }

            else -> {
                // ignore unknown messages for now
            }
        }
    }

    fun sendAck(taskId: String) {
        val ack = JSONObject().apply {
            put("type", "cmd_ack")
            put("task_id", taskId)
        }
        client?.send(ack)
    }
}
