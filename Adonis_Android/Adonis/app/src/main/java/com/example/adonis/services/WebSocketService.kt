package com.example.adonis.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.alibaba.fastjson.JSON
import com.example.adonis.entity.Code
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.Message
import com.example.adonis.entity.ReplyMessage
import com.example.adonis.entity.UserInfoMessage
import com.example.adonis.utils.Client
import java.net.URI
import java.util.UUID

class WebSocketService : Service() {

    private var replyType: Int? = null
    private lateinit var client: Client
    private val binder = SocketBinder()
    private val handler = Handler()
    private val runnable = HeartBeat()
    private val heartBeatRate: Long = 10 * 1000
    private val opMap = mutableMapOf<String, Int>()
    private val dialogueMap = mutableMapOf<String, String>()

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
        try {
            client = object : Client(uri) {
                override fun onMessage(message: String?) {
                    Log.i("Client Received", message.toString())
                    Log.i("MessageMap Before:", opMap.toString())
                    val msg =
                        JSON.parseObject(message, com.example.adonis.entity.Message::class.java)
                    if (msg.id != null) {
                        if (msg.type == FilterString.REPLY_MESSAGE) {
                            val replyMessage = msg.replyMessage
                            replyType = opMap[replyMessage.messageToReplyId]
                            if (opMap.remove(replyMessage.messageToReplyId) == null && dialogueMap.remove(replyMessage.messageToReplyId) == null) {
                                return
                            }
                            val dialogIntent = Intent(FilterString.REPLY_MESSAGE)
                            sendBroadcast(dialogIntent)
                        } else {
                            val reply = Message()
                            val replyMessage = ReplyMessage()
                            replyMessage.replyCode = Code.RECEIVED
                            replyMessage.messageToReplyId = msg.id
                            reply.id = UUID.randomUUID().toString()
                            reply.type = FilterString.REPLY_MESSAGE
                            reply.replyMessage = replyMessage
                            val replyJSON = JSON.toJSONString(reply)
                            sendMessage(replyJSON)

                            when (msg.type) {
                                FilterString.USER_INFO_MESSAGE -> {
                                    val userInfoMessage: UserInfoMessage = msg.userInfoMessage
                                    val intent = Intent(FilterString.USER_INFO_MESSAGE)
                                    intent.putExtra(FilterString.TYPE, replyType)
                                    intent.putExtra(FilterString.COED, userInfoMessage.code)
                                    sendBroadcast(intent)
                                }

                                FilterString.USER_ONLINE_MESSAGE -> {
                                    val userOnlineMessage = msg.userOnlineMessage
                                    val intent = Intent(FilterString.USER_ONLINE_MESSAGE)
                                    val initInfo = JSON.toJSONString(userOnlineMessage)
                                    intent.putExtra(FilterString.USER_ONLINE_MESSAGE, initInfo)
                                    sendBroadcast(intent)
                                }

                                FilterString.FRIEND_INFO_MESSAGE -> {
                                    val friendInfoMessage = msg.friendInfoMessage
                                    val intent = Intent(FilterString.FRIEND_INFO_MESSAGE)
                                    val jsonInfo = JSON.toJSONString(friendInfoMessage)
                                    intent.putExtra(FilterString.TYPE, replyType)
                                    intent.putExtra(FilterString.FRIEND_INFO_MESSAGE, jsonInfo)
                                    sendBroadcast(intent)
                                }

                                FilterString.DIALOGUE_INFO_MESSAGE -> {
                                    val dialogueMessage = msg.dialogueInfoMessage
                                    val intent = Intent(FilterString.DIALOGUE_INFO_MESSAGE)
                                    val jsonMessage = JSON.toJSONString(dialogueMessage)
                                    intent.putExtra(FilterString.DIALOGUE_INFO_MESSAGE, jsonMessage)
                                    sendBroadcast(intent)
                                }
                            }
                        }
                    }
                    Log.i("MessageMap After:", opMap.toString())
                    super.onMessage(message)
                }
            }
            connectServer()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
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

    fun sendMessage(message: String?, id: String?){
        if (this::client.isInitialized) {
            try {
                client.send(message)
                Log.i("Client:", message.toString())
                if (id != null)
                    dialogueMap[id.toString()] = message.toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String?, id: String?, type: Int){
        if (this::client.isInitialized) {
            try {
                client.send(message)
                Log.i("Client:", message.toString())
                if (id != null)
                    opMap[id.toString()] = type
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
    fun sendMessage(message: String?) {
        if (this::client.isInitialized) {
            try {
                client.send(message)
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
                    Log.d("Client:", "ws连接关闭，已重连")
                }
            }
            else {
                initClient()
                Log.d("Client:", "client为空，已重新初始化")
            }
            handler.postDelayed(this, heartBeatRate)
        }
    }

    override fun onDestroy() {
        closeConnect()
        super.onDestroy()
    }
}