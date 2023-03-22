package com.example.adonis.utils

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class Client(serverUri: URI?) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i("Client", "open")
    }

    override fun onMessage(message: String?) {

    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {

    }

    override fun onError(ex: Exception?) {

    }
}