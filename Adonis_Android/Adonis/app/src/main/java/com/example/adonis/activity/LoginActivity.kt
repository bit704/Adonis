package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.entity.ActionString
import com.example.adonis.entity.Code
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.Message
import com.example.adonis.entity.UserOpMessage
import com.example.adonis.services.WebSocketService
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    private val REQUEST_CODE = 1
    private val RESULT_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("Activity Info", "Login Create")

        val textUser:EditText = findViewById(R.id.edit_text_user)
        val textPassword:EditText = findViewById(R.id.edit_text_password)
        val buttonLogin:Button = findViewById(R.id.button_login)
        val buttonSignup:Button = findViewById(R.id.button_signup)

        buttonLogin.setOnClickListener {
            val id = textUser.text.toString()
            val pwd = textPassword.text.toString()

            if (id.isEmpty()) {
                val toast = Toast.makeText(this, "请输入账号！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
                return@setOnClickListener
            }
            if (pwd.isEmpty()) {
                val toast = Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
                return@setOnClickListener
            }

            val userOpMessage = UserOpMessage()
            val message = Message()
            userOpMessage.type = "sign_in"
            userOpMessage.id = id
            userOpMessage.password = pwd
            message.id = UUID.randomUUID().toString()
            message.type = FilterString.USER_OP_MESSAGE
            message.userOpMessage = userOpMessage
            val msg = JSON.toJSONString(message)
            service.sendMessage(msg, message.id, ActionString.SIGN_IN)

        }

        buttonSignup.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@LoginActivity, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        val loginConnection = LoginConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, loginConnection, BIND_AUTO_CREATE)

        intentFilter.addAction(ActionString.SIGN_IN)
        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.i("Login", "Receive")
                val replyCode = p1?.getIntExtra(FilterString.REPLY_MESSAGE, Code.DEFAULT_CODE)
                if (replyCode == 0) {
                    val editor = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).edit()
                    editor.putString("id", textUser.text.toString())
                    editor.apply()
                    val intent = Intent()
                    intent.setClass(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if (replyCode == 203) {
                    val toast = Toast.makeText(this@LoginActivity, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    toast.show()
                }
            }
        }
    }

    inner class LoginConnection: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Login DisConnect")
        }
    }

    inner class SignupConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Login DisConnect")
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> if (resultCode == RESULT_CODE){
                val result = data?.getIntExtra(FilterString.RESULT, Code.DEFAULT_CODE)
                if (result == Code.SUCCESS) {
                    val toast = Toast.makeText(this, "注册成功，请登录！", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    toast.show()
                }
            }
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