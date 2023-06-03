package com.example.adonis.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.example.adonis.utils.AdonisFunction
import com.example.adonis.utils.ChatAdapter
import com.example.adonis.utils.TimeResult
import java.util.*


class SingleChatActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()
    private lateinit var data: AdonisApplication
    private var myID: String? = null
    private var userID: String? = null

    private var setTimeLayout: RelativeLayout? = null
    private var height: Int = 0

    private val dialogueMap = mutableMapOf<String, DialogueMessage>()

    private var sendMsg: String? = null
    private val adapter = ChatAdapter()

    private lateinit var inputText: EditText

    private var lastTime = TimeResult(0, 0, 0)

    private var isChecking = false
    private var tag = false

    private val timeHandler = TimeHandler()
    private val timeThread = TimeThread()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        data = application as AdonisApplication
        myID = data.getMyID()
        height = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).getInt(FilterString.SOFT_INPUT_HEIGHT, 0)

        userID = intent.getStringExtra(FilterString.ID)
        val nickname = intent.getStringExtra(FilterString.NICKNAME)

        val backButton: Button = findViewById(R.id.button_chat_back)
        val setTimeButton: Button = findViewById(R.id.button_chat_set_time)
        inputText = findViewById(R.id.edit_text_chat_input)
        val userName: TextView = findViewById(R.id.text_chat_nickname)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_chat)
        val editLayout: LinearLayout = findViewById(R.id.layout_edit)

        val cancelButton: Button = findViewById(R.id.button_chat_cancel)
        val confirmButton: Button = findViewById(R.id.button_chat_confirm)
        val hourPicker: NumberPicker = findViewById(R.id.number_picker_chat_hours)
        val minutePicker: NumberPicker = findViewById(R.id.number_picker_chat_minutes)
        val secondPicker: NumberPicker = findViewById(R.id.number_picker_chat_seconds)

        hourPicker.maxValue = 24
        hourPicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.minValue = 0

        if (data.getLastTime().containsKey(userID)) {
            tag = true
            lastTime = AdonisFunction.longToTime(data.getLastTime()[userID!!]!!)
            hourPicker.value = lastTime.hours
            minutePicker.value = lastTime.minutes
            secondPicker.value = lastTime.seconds
        } else{
            hourPicker.value = 0
            minutePicker.value = 0
            secondPicker.value = 0
        }

        setTimeLayout = findViewById(R.id.layout_chat_set_time)
        setTimeLayout!!.layoutParams.height = height

        hourPicker.setOnValueChangedListener{ _: NumberPicker, _: Int, newVal: Int ->
            if (newVal == 24) {
                minutePicker.maxValue = 0
                secondPicker.maxValue = 0
            } else {
                minutePicker.maxValue = 59
                secondPicker.maxValue = 59
                minutePicker.value = 0
                secondPicker.value = 0
            }
        }

        cancelButton.setOnClickListener {
            hourPicker.value = 0
            minutePicker.value = 0
            secondPicker.value = 0
        }

        confirmButton.setOnClickListener {
            lastTime.setTime(hourPicker.value, minutePicker.value, secondPicker.value)
            if (tag) {
                service.updateTimeData(userID!!, AdonisFunction.timeToLong(lastTime))
            } else {
                data.getLastTime()[userID!!] = AdonisFunction.timeToLong(lastTime)
                service.insertTimeData(userID!!, AdonisFunction.timeToLong(lastTime))
            }
            setTimeLayout!!.visibility = ViewGroup.GONE
            showKeyBoards(editLayout)
        }

        @SuppressLint("HandlerLeak")
        val handler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                super.handleMessage(msg)
                when(msg.what) {
                    0 -> recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        setTimeButton.setOnClickListener {
            handler.sendEmptyMessageDelayed(0, 100)
            if (setTimeLayout!!.visibility == ViewGroup.GONE) {
                setTimeLayout!!.visibility = ViewGroup.VISIBLE
            } else {
                setTimeLayout!!.visibility = ViewGroup.GONE
            }
        }

        userName.text = nickname

        adapter.initChatList(data.findChatByID(userID), myID)

        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter

        timeThread.start()

        recyclerView.scrollToPosition(adapter.itemCount - 1)


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
                    dialogueMessage.lastedTime = AdonisFunction.timeToLong(lastTime)
                    message.id = UUID.randomUUID().toString()
                    message.type = FilterString.DIALOGUE_INFO_MESSAGE
                    message.dialogueMessage = dialogueMessage
                    val jsonMessage = JSON.toJSONString(message)
                    service.sendMessage(jsonMessage, message.id, dialogueMessage)

                    dialogueMap[message.id] = dialogueMessage
                }
            }
            true
        }


        val singleChatConnection = SingleChatConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, singleChatConnection, BIND_AUTO_CREATE)

        intentFilter.addAction(FilterString.DIALOGUE_INFO_MESSAGE)
        intentFilter.addAction(FilterString.REPLY_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.action) {
                    FilterString.REPLY_MESSAGE -> {
                        val messageID = p1.getStringExtra(FilterString.ID)
                        if (dialogueMap[messageID] != null) {
                            val dialogue = dialogueMap[messageID]
                            dialogue!!.occurredTime = p1.getLongExtra(FilterString.OCCURRED_TIME, 0)
                            adapter.addChatList(dialogue)
                            if (dialogue.lastedTime.toInt() == 0)
                                data.addNews(dialogue, userID)
                        }
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                    FilterString.DIALOGUE_INFO_MESSAGE -> {
                        val jsonMessage = p1.getStringExtra(FilterString.DIALOGUE_INFO_MESSAGE)
                        val msg = JSON.parseObject(jsonMessage, DialogueMessage::class.java)
                        adapter.addChatList(msg)
                        if (msg.lastedTime.toInt() == 0)
                            data.addNews(msg, userID)
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@SingleChatActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        backButton.setOnClickListener { finish() }

        registerReceiver(receiver, intentFilter)
    }

    inner class SingleChatConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()

            service.setOthersID(userID)
            service.checkLastTime()
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
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun showKeyBoards(view: View) {
        view.requestFocus()
        setTimeLayout!!.visibility = View.GONE
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, 0)
    }

    override fun onPause() {
        super.onPause()
        inputText.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        isChecking = false
    }

    @SuppressLint("HandlerLeak")
    inner class TimeHandler: Handler() {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            when(msg.what) {
                1 -> adapter.updateLastedNews()
            }
        }
    }

    inner class TimeThread: Thread() {
        override fun run() {
            super.run()
            isChecking = true
            while (isChecking) {
                sleep(1000)
                timeHandler.sendEmptyMessage(1)
            }
        }
    }

}