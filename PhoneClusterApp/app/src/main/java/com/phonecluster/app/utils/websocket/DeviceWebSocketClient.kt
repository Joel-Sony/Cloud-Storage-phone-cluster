package com.phonecluster.app.utils.websocket

import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DeviceWebSocketClient(
    private val serverWsUrl: String,
    private val onMessageReceived: (JSONObject) -> Unit,
    private val onDisconnected: () -> Unit
) {

    private var webSocket: WebSocket? = null

    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS) // keeps NAT alive
        .build()

    fun connect(registerPayload: JSONObject) {
        val request = Request.Builder()
            .url(serverWsUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                // Send registration / identification payload
                ws.send(registerPayload.toString())
            }

            override fun onMessage(ws: WebSocket, text: String) {
                val json = JSONObject(text)
                onMessageReceived(json)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                // not used
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                onDisconnected()
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                onDisconnected()
            }
        })
    }

    fun send(json: JSONObject) {
        webSocket?.send(json.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }
}
