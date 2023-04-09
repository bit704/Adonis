package com.example.adonis.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.alibaba.fastjson.JSON
import com.example.adonis.entity.ActionString
import com.example.adonis.entity.FilterString
import com.example.adonis.utils.Client
import java.net.URI
import java.util.*

class WebSocketService : Service() {
    private val tag = "Client"
    private lateinit var client: Client
    private val binder = SocketBinder()
    private val handler = Handler()
    private val runnable = HeartBeat()
    private val heartBeatRate: Long = 10 * 1000
    private val messageMap = mutableMapOf<String, String>()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initClient()
        handler.postDelayed(runnable, heartBeatRate)
        return super.onStartCommand(intent, flags, startId)
    }
    inner class SocketBinder: Binder() {
        fun getService():WebSocketService{
            return this@WebSocketService
        }
    }

    private fun initClient(){
        val uri = URI.create("ws://8.130.67.208:8080/ws")
        client = object : Client(uri) {
            override fun onMessage(message: String?) {
                Log.i(tag, message.toString())
                val msg = JSON.parseObject(message, com.example.adonis.entity.Message::class.java)
                if (msg.id != null) {

                    when (msg.type) {
                        FilterString.REPLY_MESSAGE -> {
                            val replyMessage = msg.replyMessage
                            val action = messageMap[replyMessage.messageToReplyId]
                            if (action != null) {
                                when (action) {
                                    ActionString.SIGN_UP -> {
                                        val intent = Intent(ActionString.SIGN_UP)
                                        intent.putExtra(
                                            FilterString.REPLY_MESSAGE,
                                            replyMessage.replyCode
                                        )
                                        sendBroadcast(intent)
                                        messageMap.remove(replyMessage.messageToReplyId)
                                    }
                                    ActionString.SIGN_IN -> {
                                        val intent = Intent(ActionString.SIGN_IN)
                                        intent.putExtra(
                                            FilterString.REPLY_MESSAGE,
                                            replyMessage.replyCode
                                        )
                                        sendBroadcast(intent)
                                        messageMap.remove(replyMessage.messageToReplyId)
                                    }
                                    ActionString.REQUEST -> {
                                        messageMap.remove(replyMessage.messageToReplyId)
                                    }
                                }
                            }
                        }

                        FilterString.USER_ONLINE_MESSAGE -> {
                            val userOnlineMessage = msg.userOnlineMessage
                            val intent = Intent(ActionString.MAIN_INFO)
                            val initInfo = JSON.toJSONString(userOnlineMessage)
                            intent.putExtra("type", FilterString.USER_ONLINE_MESSAGE)
                            intent.putExtra(FilterString.USER_ONLINE_MESSAGE, initInfo)
                            sendBroadcast(intent)
                        }

                        FilterString.FRIEND_INFO_MESSAGE -> {

                        }

                        FilterString.DIALOGUE_INFO_MESSAGE -> {

                        }

                    }
                }
                super.onMessage(message)
            }
        }
        connectServer()
    }

    private fun connectServer(){
        Thread {
            try {
                client.connectBlocking()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun reconnectServer() {
        handler.removeCallbacks(runnable)
        Thread {
            try {
                client.connectBlocking()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }
    fun sendMessage(message: String?, id: String?, type: String?){
        if (this::client.isInitialized) {
            try {
                client.send(message)
                Log.i(tag, message.toString())
                if (id != null)
                    messageMap[id.toString()] = type.toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String?) {
        if (this::client.isInitialized) {
            try {
                client.send(message)
                Log.i(tag, message.toString())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun closeConnect(){
        handler.removeCallbacks(runnable)
        try {
            if (this::client.isInitialized){
                client.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class HeartBeat: Runnable{
        override fun run() {
            if (this@WebSocketService::client.isInitialized) {
                if (client.isClosed) {
                    reconnectServer()
                    Log.d(tag, "ws连接关闭，已重连")
                }
            }
            else {
                initClient()
                Log.d(tag, "client为空，已重新初始化")
            }
            handler.postDelayed(this, heartBeatRate)
        }
    }

    override fun onDestroy() {
        closeConnect()
        super.onDestroy()
    }
}