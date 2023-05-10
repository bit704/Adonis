package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.ActionString

import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.FriendOpMessage
import com.example.adonis.entity.Message
import com.example.adonis.entity.MessageCode
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

    private val addedFriends = mutableListOf<FriendInfoMessage>()

    private lateinit var data: AdonisApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friends)

        data = application as AdonisApplication

        val serviceIntent = Intent(this, WebSocketService::class.java)
        val connection = NewFriendsConnection()
        bindService(serviceIntent, connection, BIND_AUTO_CREATE)

        userId = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).getString(FilterString.ID, null)

        val backButton: Button = findViewById(R.id.button_new_friends_back)
        val moreButton: Button = findViewById(R.id.button_new_friends_more)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_new_friends)
        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout

        adapter.setAgreeButtonClickedListener (object : NewFriendsAdapter.OnAgreeButtonClickedListener {
            override fun onAgreeButtonClick(holder: NewFriendsAdapter.NewFriendsViewHolder) {
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
        recyclerView.adapter = adapter

        adapter.initNewFriendsList(data.getNewFriends())
        adapter.notifyDataSetChanged()

        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val jsonInfo = p1?.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                val info = JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                if (info.code == MessageCode.FIF_OP_SUCCESS.id) {

                }
            }
        }

        moreButton.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
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