package com.example.adonis.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.adonis.utils.Client
import java.net.URI

class WebSocketService : Service() {

    private val uri = URI.create("ws://8.130.67.208:8080/ws")
    private lateinit var client: Client
    private val binder = SocketBinder()

    override fun onBind(p0: Intent?): IBinder? {
        Log.i("Service Info", "Service Bind")
        return binder
    }

    override fun onCreate() {
        client = object : Client(uri) {
            override fun onMessage(message: String?) {
                Log.i("msg", message.toString())
                super.onMessage(message)
            }
        }
        client.connect()
        Log.i("Client", client.isOpen.toString())


        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Service Info","Service Start")

        return super.onStartCommand(intent, flags, startId)
    }
    inner class SocketBinder: Binder() {
        fun getService():WebSocketService{
            return this@WebSocketService
        }
    }

    fun getClient():Client{
        return client
    }

}