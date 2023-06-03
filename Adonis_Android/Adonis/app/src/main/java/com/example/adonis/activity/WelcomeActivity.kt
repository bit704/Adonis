package com.example.adonis.activity

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import java.util.*


class WelcomeActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()
    private var success: Boolean = false
    private var received: Boolean = false
    private lateinit var myID: String
    private lateinit var myPWD: String
    private var onlineTag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val intent = Intent(this, WebSocketService::class.java)
        startService(intent)

        val welcomeConnection = WelcomeConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, welcomeConnection, BIND_AUTO_CREATE)

        val data = application as AdonisApplication
        val sharedPreferences = getSharedPreferences(FilterString.DATA, MODE_PRIVATE)
        val autoLoginFlag: Boolean = sharedPreferences.getBoolean(FilterString.IF_ONLINE, false)

        intentFilter.addAction(FilterString.USER_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)

        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                received = true
                data.initMyID(myID)
                service.initMyID(myID)
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        onlineTag = false
                        success = true
                    }
                    FilterString.USER_INFO_MESSAGE -> {
                        val code = p1.getIntExtra(FilterString.CODE, Code.DEFAULT_CODE)
                        if (code == 200) {
                            success = true
                        }
                    }
                }
            }
        }

        if (autoLoginFlag) {
            Thread {
                while (!received) {
                    if (this::service.isInitialized) {
                        myID = sharedPreferences.getString(FilterString.ID, null).toString()
                        myPWD = sharedPreferences.getString(FilterString.PASSWORD, null).toString()
                        val userOpMessage = UserOpMessage()
                        val message = Message()
                        userOpMessage.code = MessageCode.UOP_SIGN_IN.id
                        userOpMessage.id = myID
                        userOpMessage.password = myPWD
                        message.id = UUID.randomUUID().toString()
                        message.type = FilterString.USER_OP_MESSAGE
                        message.userOpMessage = userOpMessage
                        val msg = JSON.toJSONString(message)
                        service.sendMessage(msg, message.id, MessageCode.UOP_SIGN_IN.id)
                    }
                    Thread.sleep(1000)
                }
                if (success) {
                    val it = Intent(this@WelcomeActivity, MainActivity::class.java)
                    it.putExtra(FilterString.ONLINE_STATE, onlineTag)
                    startActivity(it)
                } else {
                    val it = Intent(this@WelcomeActivity, LoginActivity::class.java)
                    it.putExtra(FilterString.AUTO_LOGIN, true)
                    startActivity(it)
                }
            }.start()
        }

        if (!autoLoginFlag) {
            val handler = Handler()
            handler.postDelayed({
                val it = Intent(this@WelcomeActivity, LoginActivity::class.java)
                it.putExtra(FilterString.AUTO_LOGIN, false)
                startActivity(it)
                this@WelcomeActivity.finish()
            }, 1000 * 3)
        }
    }

    inner class WelcomeConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

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