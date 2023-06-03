package com.example.adonis.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.alibaba.fastjson.JSON
import com.example.adonis.entity.Code
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.Message
import com.example.adonis.entity.MessageCode
import com.example.adonis.entity.ReplyMessage
import com.example.adonis.entity.UserInfoMessage
import com.example.adonis.utils.DataBaseHelper
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.UUID

class WebSocketService : Service() {

    private var replyType: Int? = Code.DEFAULT_CODE
    private lateinit var client: AdonisClient
    private val binder = SocketBinder()
    private val handler = Handler()
    private val runnable = HeartBeat()
    private val heartBeatRate: Long = 10 * 1000

    private val opMap = mutableMapOf<String, Int>()
    private val dialogueMap = mutableMapOf<String, DialogueMessage>()
//    private val lastedMap = mutableMapOf<String, MutableList<DialogueMessage>>()

    private var othersID: String? = null
    private var myID: String? = null
    private var myDB: SQLiteDatabase? = null
    @SuppressLint("SdCardPath")
    private val dbPATH = "/data/data/com.example.adonis/databases/"
    private var isChecking = false
    private val lastedUser = mutableSetOf<String>()

    override fun onBind(p0: Intent?): IBinder {
        Log.i("Service", p0.toString() + "bind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Service", "Start")
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
            client = AdonisClient(uri)
            connectServer()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    inner class AdonisClient(serverUri: URI?): WebSocketClient(serverUri) {
        override fun onMessage(message: String?) {
            Log.i("Client Received", message.toString())
            Log.i("MessageMap Before:", opMap.toString())
            val msg =
                JSON.parseObject(message, com.example.adonis.entity.Message::class.java)
            if (msg.id != null) {
                if (msg.type == FilterString.REPLY_MESSAGE) {
                    val replyMessage = msg.replyMessage
                    replyType = opMap[replyMessage.messageToReplyId]
                    opMap.remove(replyMessage.messageToReplyId)
                    if (dialogueMap.containsKey(replyMessage.messageToReplyId)) {
                        val dialogueMessage = dialogueMap[replyMessage.messageToReplyId]

                        val id: String = if (dialogueMessage!!.senderId != myID) {
                            dialogueMessage.senderId
                        } else {
                            dialogueMessage.receiverId
                        }

                        dialogueMap.remove(replyMessage.messageToReplyId)
                        if (id == myID)
                            return

                        dialogueMessage.occurredTime = replyMessage.occurredTime
                        if (dialogueMessage.lastedTime > 0) {
                            insertData(FilterString.LASTED_NEWS_TABLE, dialogueMessage)
                            checkLastTime()
                        } else {
                            insertData(FilterString.NEWS_TABLE, dialogueMessage)
                        }

                        Thread {
                            if (dialogueMessage.lastedTime <= 0) {
                                if (DataBaseHelper.queryData(
                                        myDB!!,
                                        FilterString.LATEST_NEWS_TABLE,
                                        id,
                                        myID
                                    )
                                ) {
                                    updateData(FilterString.LATEST_NEWS_TABLE, id, dialogueMessage)
                                } else {
                                    insertData(FilterString.LATEST_NEWS_TABLE, dialogueMessage)
                                }
                            }
                        }.start()

                        val dialogIntent = Intent(FilterString.REPLY_MESSAGE)
                        dialogIntent.putExtra(FilterString.ID, replyMessage.messageToReplyId)
                        dialogIntent.putExtra(FilterString.OCCURRED_TIME, replyMessage.occurredTime)
                        sendBroadcast(dialogIntent)

                        val updateNews = Intent(FilterString.UPDATE_NEWS)
                        updateNews.putExtra(
                            FilterString.DIALOGUE_INFO_MESSAGE,
                            JSON.toJSONString(dialogueMessage)
                        )
                        sendBroadcast(updateNews)
                    }

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
                            if (userInfoMessage.code == MessageCode.UIF_OP_SUCCESS.id) {

                            }
                            val intent = Intent(FilterString.USER_INFO_MESSAGE)
                            intent.putExtra(FilterString.TYPE, replyType)
                            intent.putExtra(FilterString.CODE, userInfoMessage.code)
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
                            val friendInfo = msg.friendInfoMessage
                            Log.i("Debug", replyType.toString())
                            if (friendInfo.code == MessageCode.FIF_OP_SUCCESS.id) {
                                val temp = FriendInfoMessage(friendInfo)
                                when (replyType) {
                                    MessageCode.FOP_ADD.id -> {
                                        temp.code = MessageCode.FIF_ADD_TO.id
                                        insertData(FilterString.NEW_FRIENDS_TABLE, temp)
                                    }
                                    MessageCode.FOP_CONSENT.id -> {
                                        temp.code = MessageCode.FIF_TWO_WAY.id
                                        insertData(FilterString.CONTACTS_TABLE, temp)
                                        temp.code = MessageCode.FOP_CONSENT.id
                                        updateData(FilterString.NEW_FRIENDS_TABLE, data = temp)
                                    }
                                    MessageCode.FOP_REJECT.id -> {
                                        temp.code = MessageCode.FOP_REJECT.id
                                        updateData(FilterString.NEW_FRIENDS_TABLE, data = temp)
                                    }
                                    MessageCode.FOP_DELETE.id -> {
                                        deleteData(
                                            FilterString.CONTACTS_TABLE,
                                            friendInfo.id,
                                            data = temp
                                        )
                                    }
                                    MessageCode.FOP_CUSTOM_NICKNAME.id -> {
                                        updateData(FilterString.CONTACTS_TABLE, data = temp)

                                    }
                                }
                            }
                            if (friendInfo.code == MessageCode.FIF_ADD_CONSENT.id) {
                                val temp = FriendInfoMessage(friendInfo)
                                temp.code = MessageCode.FIF_TWO_WAY.id
                                insertData(FilterString.CONTACTS_TABLE, temp)
                                temp.code = MessageCode.FIF_ADD_CONSENT.id
                                updateData(FilterString.NEW_FRIENDS_TABLE, data = temp)
                            }
                            if (friendInfo.code == MessageCode.FIF_REJECT.id) {
                                updateData(FilterString.NEW_FRIENDS_TABLE, data = friendInfo)
                            }
                            if (friendInfo.code == MessageCode.FIF_ADD_YOU.id) {
                                updateData(
                                    FilterString.NEW_FRIENDS_TABLE,
                                    othersID = friendInfo.id,
                                    data = friendInfo
                                )
                            }

                            val intent = Intent(FilterString.FRIEND_INFO_MESSAGE)
                            val jsonInfo = JSON.toJSONString(friendInfo)
                            intent.putExtra(FilterString.TYPE, replyType)
                            intent.putExtra(FilterString.FRIEND_INFO_MESSAGE, jsonInfo)
                            sendBroadcast(intent)
                        }

                        FilterString.DIALOGUE_INFO_MESSAGE -> {
                            val dialogueMessage = msg.dialogueMessage

                            val id: String = if (dialogueMessage.senderId != myID) {
                                dialogueMessage.senderId
                            } else {
                                dialogueMessage.receiverId
                            }

                            if (dialogueMessage.lastedTime > 0) {
                                if (id == othersID) {
                                    insertData(
                                        FilterString.LASTED_NEWS_TABLE,
                                        dialogueMessage
                                    )
                                    checkLastTime()
                                }
                            } else {
                                insertData(FilterString.NEWS_TABLE, dialogueMessage)
                            }

                            if (id != othersID) {
                                insertData(FilterString.UNREAD_NEWS_TABLE, dialogueMessage)
                            }

                            Thread {
                                if (dialogueMessage.lastedTime <= 0) {
                                    if (DataBaseHelper.queryData(
                                            myDB!!,
                                            FilterString.LATEST_NEWS_TABLE,
                                            id,
                                            myID
                                        )
                                    ) {
                                        updateData(
                                            FilterString.LATEST_NEWS_TABLE,
                                            id,
                                            dialogueMessage
                                        )
                                    } else {
                                        insertData(FilterString.LATEST_NEWS_TABLE, dialogueMessage)
                                    }
                                }
                            }.start()

                            val intent = Intent(FilterString.DIALOGUE_INFO_MESSAGE)
                            val jsonMessage = JSON.toJSONString(dialogueMessage)
                            intent.putExtra(FilterString.DIALOGUE_INFO_MESSAGE, jsonMessage)
                            sendBroadcast(intent)
                        }
                    }
                }
            }
            Log.i("MessageMap After:", opMap.toString())

        }
        override fun onOpen(handshakedata: ServerHandshake?) {sendOnLineStateBroadcast(true)}
        override fun onClose(code: Int, reason: String?, remote: Boolean) {sendOnLineStateBroadcast(false)}
        override fun onError(ex: java.lang.Exception?) {}
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
                client.reconnectBlocking()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun sendMessage(message: String?, id: String?, dialogue: DialogueMessage){
        if (!client.readyState.equals(ReadyState.OPEN)) {
            sendOffLineBroadCast()
        } else {
            try {
                client.send(message)
                Log.i("Client:", message.toString())
                if (id != null)
                    dialogueMap[id.toString()] = dialogue
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String?, id: String?, type: Int){
        if (this::client.isInitialized) {
            if (!client.readyState.equals(ReadyState.OPEN)) {
                sendOffLineBroadCast()
            } else {
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
    }
    fun sendMessage(message: String?) {
        if (this::client.isInitialized && client.readyState.equals(ReadyState.OPEN)) {
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
                if (client.readyState.equals(ReadyState.CLOSING)
                    || client.readyState.equals(ReadyState.CLOSED)) {
                    reconnectServer()
                    Log.d("Client:", "ws连接关闭，已重连")
                } else if (client.readyState.equals(ReadyState.NOT_YET_CONNECTED)) {
                    connectServer()
                    Log.d("Client:", "ws未连接，已连接")
                }
            }
            else {
                initClient()
                Log.d("Client:", "client为空，已重新初始化")
            }
            handler.postDelayed(this, heartBeatRate)
        }
    }

    fun initMyID(id: String) {myID = id}
    fun setOthersID(id: String?) {othersID = id}

    fun initDataBase() {
        val dbName = "$myID.db"
        Log.i("DBExist", DataBaseHelper.checkDBExist(dbName).toString())
        myDB = if (DataBaseHelper.checkDBExist(dbName)) {
            val path = dbPATH + dbName
            SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE)
        } else {
            val helper = DataBaseHelper(this, dbName, null, 1)
            helper.writableDatabase
        }
    }

    fun checkLastTime() {
        if (isChecking)
            return
        isChecking = true
        Thread {
            while (true) {
                Thread.sleep(1000)
                if (!DataBaseHelper.updateLastedTime(myDB!!))
                    break
            }
            isChecking = false
        }.start()
    }

    fun getDataBase(): SQLiteDatabase? { return myDB }

    fun <T>insertData(table: String, data: T?) {
        Thread {
            DataBaseHelper.insertData(myDB!!, table, data)
        }.start()
    }

    fun insertTimeData(id: String, time: Long) {
        Thread {
            DataBaseHelper.insertTimeData(myDB!!, time, id)
        }.start()
    }

    fun updateTimeData(id: String, time: Long) {
        Thread {
            DataBaseHelper.updateTimeData(myDB!!, time, id)
        }.start()
    }

    fun <T>updateData(table: String, othersID: String? = null, data: T?) {
        Thread {
            DataBaseHelper.updateData(myDB!!, table, othersID, data)
        }.start()
    }

    fun <T>deleteData(table: String, othersID: String? = null, data: T?) {
        Thread {
            DataBaseHelper.deleteData(myDB!!, table, othersID, data)
        }.start()
    }

    fun updateUnread(userID: String?){
        DataBaseHelper.updateUnread(myDB!!, userID, myID)
    }

    override fun onDestroy() {
        closeConnect()
        myDB?.close()
        super.onDestroy()
    }

    private fun sendOffLineBroadCast() {
        val intent = Intent(FilterString.OFF_LINE)
        sendBroadcast(intent)
    }

    private fun sendOnLineStateBroadcast(tag: Boolean) {
        val intent = Intent(FilterString.ONLINE_STATE)
        intent.putExtra(FilterString.ONLINE_STATE, tag)
        sendBroadcast(intent)
    }

}