package com.example.adonis.activity

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.Message
import com.example.adonis.services.WebSocketService
import com.example.adonis.utils.ChatAdapter
import java.util.*


class SingleChatActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()
    private lateinit var data: AdonisApplication
    private var myID: String? = null

    private var sendMsg: String? = null
    private val adapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)

        data = application as AdonisApplication
        myID = data.getMyID()

        val userID = intent.getStringExtra(FilterString.ID)
        val nickname = intent.getStringExtra(FilterString.NICKNAME)

        val fileButton: Button = findViewById(R.id.button_chat_files)
        val inputText: EditText = findViewById(R.id.edit_text_chat_input)
        val userName: TextView = findViewById(R.id.text_chat_nickname)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_chat)
        val editLayout: LinearLayout = findViewById(R.id.layout_edit)

        val handler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                super.handleMessage(msg)
                when(msg.what) {
                    0 -> recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        userName.text = nickname

        adapter.initChatList(data.findChatByID(userID), myID)

        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter

        editLayout.setOnClickListener {
            showKeyBoards(inputText)
            handler.sendEmptyMessageDelayed(0, 100)
        }

        inputText.setOnEditorActionListener{ _,action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND) {
                val inputMsg = inputText.text.toString()
                if (inputMsg.isNotEmpty()) {
                    inputText.text.clear()
                    val message = Message()
                    val dialogueMessage = DialogueMessage()
                    dialogueMessage.content = inputMsg
                    sendMsg = inputMsg
                    dialogueMessage.senderId = myID
                    dialogueMessage.receiverId = userID
                    message.id = UUID.randomUUID().toString()
                    message.type = FilterString.DIALOGUE_INFO_MESSAGE
                    message.dialogueInfoMessage = dialogueMessage
                    val jsonMessage = JSON.toJSONString(message)
                    service.sendMessage(jsonMessage, message.id)
                    adapter.addChatList(dialogueMessage)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)

                    val updateNews = Intent(FilterString.UPDATE_NEWS)
                    updateNews.putExtra(FilterString.DIALOGUE_INFO_MESSAGE, JSON.toJSONString(dialogueMessage))
                    sendBroadcast(updateNews)
                }
            }
            true
        }

        fileButton.setOnClickListener {

        }

        val singleChatConnection = SingleChatConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, singleChatConnection, BIND_AUTO_CREATE)

        intentFilter.addAction(FilterString.DIALOGUE_INFO_MESSAGE)
        intentFilter.addAction(FilterString.REPLY_MESSAGE)
        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.action) {
                    FilterString.REPLY_MESSAGE -> {

                    }
                    FilterString.DIALOGUE_INFO_MESSAGE -> {

                        val jsonMessage = p1.getStringExtra(FilterString.DIALOGUE_INFO_MESSAGE)
                        val msg = JSON.parseObject(jsonMessage, DialogueMessage::class.java)
                        adapter.addChatList(msg)
                    }
                }
            }
        }
    }

    inner class SingleChatConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "SingleChat DisConnect")
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

    private fun showKeyBoards(view: View) {
        view.requestFocus()
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, 0)
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