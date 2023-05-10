package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import com.example.adonis.utils.AddAdapter
import java.util.*

class AddActivity : AppCompatActivity() {
    private val adapter = AddAdapter()
    private var userId: String? = null
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val serviceIntent = Intent(this, WebSocketService::class.java)
        val addConnection = AddConnection()
        bindService(serviceIntent, addConnection, BIND_AUTO_CREATE)


        userId = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).getString(FilterString.ID, null)

        val notExist: TextView = findViewById(R.id.add_not_exist)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_results)
        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        adapter.setAddButtonClickListener(object : AddAdapter.OnAddButtonClickListener{
            override fun onAddButtonClick(holder: AddAdapter.AddViewHolder) {
                val intent = Intent(this@AddActivity, ConfirmActivity::class.java)
                intent.putExtra(FilterString.ID, holder.user.text.toString())
                intent.putExtra(FilterString.NICKNAME, holder.nickname.text.toString())
                startActivity(intent)
            }
        })

        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter

        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val jsonInfo = p1?.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                val friendInfoMessage = JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                val status = friendInfoMessage.code
                if (status == MessageCode.FIF_EXIST.id) {
                    adapter.showResult(friendInfoMessage)
                    adapter.notifyDataSetChanged()
                    notExist.visibility = TextView.INVISIBLE
                }
                else if (status == MessageCode.FIF_NOT_EXIST.id) {
                    adapter.showNotExistResult()
                    adapter.notifyDataSetChanged()
                    notExist.visibility = TextView.VISIBLE
                }
            }
        }

        val backButton = findViewById<Button>(R.id.button_add_back)
        backButton.setOnClickListener { finish() }
        val search = findViewById<EditText>(R.id.edit_text_search_add)
        search.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                val searchId = search.text.toString()
                if (searchId.isNotEmpty()) {
                    val message = Message()
                    val friendOpMessage = FriendOpMessage()
                    friendOpMessage.subjectId = userId
                    friendOpMessage.objectId = searchId
                    friendOpMessage.code = MessageCode.FOP_QUERY_EXIST.id
                    message.id = UUID.randomUUID().toString()
                    message.type = FilterString.FRIEND_OP_MESSAGE
                    message.friendOpMessage = friendOpMessage
                    val msg = JSON.toJSONString(message)
                    service.sendMessage(msg, message.id, MessageCode.FOP_QUERY_EXIST.id)

                    hideKeyBoards()
                }
            }
            false
        }

    }

    inner class AddConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Add DisConnect")
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

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun hideKeyBoards() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}