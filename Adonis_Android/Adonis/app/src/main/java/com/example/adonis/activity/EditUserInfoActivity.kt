package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import java.util.*

class EditUserInfoActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    private lateinit var data: AdonisApplication
    private var isBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)

        data = application as AdonisApplication
        val myID = data.getMyID()
        val userID = intent.getStringExtra(FilterString.ID)
        val customNickname = intent.getStringExtra(FilterString.CUSTOM_NICKNAME)
        val nickname = intent.getStringExtra(FilterString.NICKNAME)

        val editText: EditText = findViewById(R.id.edit_info_remark)

        val name = if (customNickname!!.isEmpty()) {
            nickname
        } else {
            customNickname
        }

        editText.setText(name)

        val userInfoConnection = EditUserInfoConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, userInfoConnection, BIND_AUTO_CREATE)

        val deleteButton: Button = findViewById(R.id.button_user_info_delete)
        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(FilterString.DELETE_FRIEND)
                .setMessage(FilterString.makeDeleteDialogue(name))
                .setPositiveButton(FilterString.CONFIRM) { _, _ ->
                    val message = Message()
                    val friendOpMessage = FriendOpMessage()
                    friendOpMessage.code = MessageCode.FOP_DELETE.id
                    friendOpMessage.subjectId = myID
                    friendOpMessage.objectId = userID
                    message.id = UUID.randomUUID().toString()
                    message.type = FilterString.FRIEND_OP_MESSAGE
                    message.friendOpMessage = friendOpMessage
                    val msgJSON = JSON.toJSONString(message)
                    service.sendMessage(msgJSON, message.id, MessageCode.FOP_DELETE.id)
                }
                .setNegativeButton(FilterString.CANCEl, null)
                .create()
                .show()
        }

        val switch: SwitchCompat = findViewById(R.id.switch_edit_info)
        val confirmButton: Button = findViewById(R.id.button_edit_info_done)
        confirmButton.setOnClickListener {
            if (switch.isChecked) {
                isBlocked = true
            }
            val message = Message()
            val friendOpMessage = FriendOpMessage()
            friendOpMessage.code = MessageCode.FOP_CUSTOM_NICKNAME.id
            friendOpMessage.subjectId = myID
            friendOpMessage.objectId = userID
            friendOpMessage.customNickname = editText.text.toString()
            message.id = UUID.randomUUID().toString()
            message.type = FilterString.FRIEND_OP_MESSAGE
            message.friendOpMessage = friendOpMessage
            val msgJSON = JSON.toJSONString(message)
            service.sendMessage(msgJSON, message.id, MessageCode.FOP_CUSTOM_NICKNAME.id)
        }


        switch.setOnCheckedChangeListener { button, _ ->
            if (button.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle(FilterString.BLOCK_FRIEND)
                    .setMessage(FilterString.BLOCK_INFO)
                    .setPositiveButton(FilterString.CONFIRM, null)
                    .setNegativeButton(FilterString.CANCEl) { _, _ ->
                        button.isChecked = false
                    }
                    .create()
                    .show()
            }
        }

        val backButton: Button = findViewById(R.id.button_edit_info_back)
        backButton.setOnClickListener { finish() }

        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@EditUserInfoActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT)
                            .show()
                    }
                    FilterString.FRIEND_INFO_MESSAGE -> {
                        val jsonInfo = p1.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                        val type = p1.getIntExtra(FilterString.TYPE, Code.DEFAULT_CODE)
                        val friendInfoMessage =
                            JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                        if (friendInfoMessage.code == MessageCode.FIF_OP_SUCCESS.id) {
                            when (type) {
                                MessageCode.FOP_DELETE.id -> {
                                    setResult(Code.DELETE_RESULT_CODE)
                                    finish()
                                }
                                MessageCode.FOP_CUSTOM_NICKNAME.id -> {
                                    if (isBlocked) {
                                        val message = Message()
                                        val friendOpMessage = FriendOpMessage()
                                        friendOpMessage.code = MessageCode.FOP_BLOCK.id
                                        friendOpMessage.subjectId = myID
                                        friendOpMessage.objectId = userID
                                        friendOpMessage.customNickname = editText.text.toString()
                                        message.id = UUID.randomUUID().toString()
                                        message.type = FilterString.FRIEND_OP_MESSAGE
                                        message.friendOpMessage = friendOpMessage
                                        val msgJSON = JSON.toJSONString(message)
                                        service.sendMessage(
                                            msgJSON,
                                            message.id,
                                            MessageCode.FOP_BLOCK.id
                                        )
                                    } else {
                                        intent.putExtra(
                                            FilterString.DATA,
                                            JSON.toJSONString(friendInfoMessage)
                                        )
                                        setResult(Code.NICKNAME_RESULT_CODE, intent)
                                        finish()
                                    }
                                }
                                MessageCode.FOP_BLOCK.id -> {
                                    intent.putExtra(
                                        FilterString.DATA,
                                        JSON.toJSONString(friendInfoMessage)
                                    )
                                    setResult(Code.NICKNAME_RESULT_CODE, intent)
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    inner class EditUserInfoConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null) {
                val pos = IntArray(2)
                view.getLocationInWindow(pos)
                val left = pos[0]
                val top = pos[1]
                val right = left + view.width
                val bottom = top + view.height
                if (!(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)) {
                    hideKeyBoards()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyBoards() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
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



