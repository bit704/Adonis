package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*

import com.example.adonis.services.WebSocketService
import com.example.adonis.utils.NewFriendsAdapter
import java.util.UUID

class NewFriendsActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    private var userId: String? = null
    private val adapter = NewFriendsAdapter()

    private lateinit var data: AdonisApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friends)

        data = application as AdonisApplication

        val serviceIntent = Intent(this, WebSocketService::class.java)
        val connection = NewFriendsConnection()
        bindService(serviceIntent, connection, BIND_AUTO_CREATE)

        userId = data.getMyID()

        val backButton: Button = findViewById(R.id.button_new_friends_back)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_new_friends)
        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout


        adapter.setAgreeButtonClickedListener (object : NewFriendsAdapter.OnAgreeButtonClickedListener {
            override fun onAgreeButtonClick(
                holder: NewFriendsAdapter.NewFriendsViewHolder,
                position: Int
            ) {
                val message = Message()
                val friendOpMessage = FriendOpMessage()
                friendOpMessage.code = MessageCode.FOP_CONSENT.id
                friendOpMessage.subjectId = userId
                friendOpMessage.objectId = holder.id
                message.id = UUID.randomUUID().toString()
                message.type = FilterString.FRIEND_OP_MESSAGE
                message.friendOpMessage = friendOpMessage
                val jsonMessage = JSON.toJSONString(message)
                service.sendMessage(jsonMessage, message.id, MessageCode.FOP_CONSENT.id)
            }
        })
        adapter.setRejectButtonClickedListener(object : NewFriendsAdapter.OnRejectButtonClickedListener {
            override fun onRejectButtonClick(
                holder: NewFriendsAdapter.NewFriendsViewHolder,
                position: Int
            ) {
                val message = Message()
                val friendOpMessage = FriendOpMessage()
                friendOpMessage.code = MessageCode.FOP_REJECT.id
                friendOpMessage.subjectId = userId
                friendOpMessage.objectId = holder.id
                message.id = UUID.randomUUID().toString()
                message.type = FilterString.FRIEND_OP_MESSAGE
                message.friendOpMessage = friendOpMessage
                val jsonMessage = JSON.toJSONString(message)
                service.sendMessage(jsonMessage, message.id, MessageCode.FOP_REJECT.id)
            }

        })
        recyclerView.adapter = adapter

        adapter.initNewFriendsList(data.getNewFriends())

        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@NewFriendsActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT)
                            .show()
                    }
                    FilterString.FRIEND_INFO_MESSAGE -> {
                        val jsonInfo = p1?.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                        val type = p1?.getIntExtra(FilterString.TYPE, Code.DEFAULT_CODE)
                        val info = JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                        if (info.code == MessageCode.FIF_OP_SUCCESS.id) {
                            if (type == MessageCode.FOP_CONSENT.id) {
                                info.code = MessageCode.FOP_CONSENT.id
                                adapter.updateList(info)
                            }
                            if (type == MessageCode.FOP_REJECT.id) {
                                info.code = MessageCode.FOP_REJECT.id
                                adapter.updateList(info)
                            }
                        }
                    }
                }
            }
        }


        backButton.setOnClickListener { finish() }
    }

    inner class NewFriendsConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "NewFriends DisConnect")
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}