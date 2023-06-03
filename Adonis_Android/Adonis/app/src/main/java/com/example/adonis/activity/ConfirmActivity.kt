package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import org.json.JSONArray
import java.util.UUID

class ConfirmActivity : AppCompatActivity() {
    private var userId: String? = null
    private var friendId: String? = null
    private var friendNickname: String? = null

    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val user = JSON.parseObject(intent.getStringExtra(FilterString.DATA), FriendInfoMessage::class.java)
        friendId = user.id
        friendNickname = user.nickname
        userId = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).getString(FilterString.ID, null)

        val id: TextView = findViewById(R.id.confirm_id)
        val nickname: TextView = findViewById(R.id.confirm_nickname)
        val remark: EditText = findViewById(R.id.confirm_remark)
        val requestContent: EditText = findViewById(R.id.confirm_request)
        val confirm: Button = findViewById(R.id.button_confirm_send)
        val back: Button = findViewById(R.id.button_confirm_back)

        back.setOnClickListener { finish() }

        nickname.text = friendNickname
        id.text = friendId
        remark.setText(friendNickname)

        val serviceIntent = Intent(this, WebSocketService::class.java)
        val connection = ConfirmConnection()
        bindService(serviceIntent, connection, BIND_AUTO_CREATE)

        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@ConfirmActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT)
                            .show()
                    }
                    FilterString.FRIEND_INFO_MESSAGE -> {
                        val jsonInfo = p1.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                        val friendInfoMessage =
                            JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                        val code = friendInfoMessage.code
                        if (code == MessageCode.FIF_OP_SUCCESS.id) {
                            finish()
                        }
                    }
                }
            }
        }

        confirm.setOnClickListener {
            val message = Message()
            val friendOpMessage = FriendOpMessage()
            friendOpMessage.code = MessageCode.FOP_ADD.id
            friendOpMessage.subjectId = userId
            friendOpMessage.objectId = friendId
            friendOpMessage.customNickname = remark.text.toString()
            friendOpMessage.memo = requestContent.text.toString()
            message.id = UUID.randomUUID().toString()
            message.type = FilterString.FRIEND_OP_MESSAGE
            message.friendOpMessage = friendOpMessage
            val msgJSON = JSON.toJSONString(message)
            service.sendMessage(msgJSON, message.id, MessageCode.FOP_ADD.id)
            hideKeyBoards()
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

    inner class ConfirmConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Add DisConnect")
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