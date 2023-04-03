package com.example.adonis.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adonis.R
import com.example.adonis.services.WebSocketService
import com.example.adonis.utils.Client
import com.alibaba.fastjson.*
import com.example.adonis.entity.Message
import com.example.adonis.entity.ReplyCode
import com.example.adonis.entity.UserInfoMessage
import java.util.UUID


class SignupActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var client: Client
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val nameEditText: EditText = findViewById(R.id.edit_text_name_signup)
        val userEditText: EditText = findViewById(R.id.edit_text_user_signup)
        val pwdEditText: EditText = findViewById(R.id.edit_text_password_signup)
        val confirmEditText: EditText = findViewById(R.id.edit_text_password_confirm)
        val confirmButton: Button = findViewById(R.id.button_signup_confirm)

        val inputFilter = EditTextFilter()
        val editTextFilters: Array<InputFilter> = arrayOf(inputFilter)
        userEditText.filters = editTextFilters

        val newConnection = SignupConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, newConnection, BIND_AUTO_CREATE)

        confirmButton.setOnClickListener {
            val userInfoMessage = UserInfoMessage()
            val message = Message()
            val nickname = nameEditText.text.toString()
            val id = userEditText.text.toString()
            val pwd = pwdEditText.text.toString()
            val confirmPwd = confirmEditText.text.toString()
            if (nickname.isEmpty()) {
                val toast = Toast.makeText(this, "昵称不能为空！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
                return@setOnClickListener
            }
            if (id.isEmpty()) {
                val toast = Toast.makeText(this, "账号不能为空！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
                return@setOnClickListener
            }
            if (pwd.isEmpty()) {
                val toast = Toast.makeText(this, "请设置密码！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
                return@setOnClickListener
            }
            if (pwd != confirmPwd) {
                val toast = Toast.makeText(this, "两次输入密码不一致！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                toast.show()
            }
            else {
                userInfoMessage.type = "sign_up"
                userInfoMessage.id = id
                userInfoMessage.nickname = nickname
                userInfoMessage.password = pwd
                message.type = "UserInfoMessage"
                message.id = UUID.randomUUID().toString()
                message.userInfoMessage = userInfoMessage
                val msg = JSON.toJSONString(message)
                Log.i("msg", msg.toString())
                client.send(msg)
            }
        }
    }

    inner class SignupConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            client = binder.getService().getClient()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Login DisConnect")
        }

    }

    inner class EditTextFilter: InputFilter{
        override fun filter(
            p0: CharSequence?,
            p1: Int,
            p2: Int,
            p3: Spanned?,
            p4: Int,
            p5: Int
        ): String? {
            val regex = Regex("[\\s|\u4E00-\u9FA5]")
            Regex
            val isChinese = regex.containsMatchIn(p0.toString())
            if( isChinese ) {
                return ""
            }
            return null
        }
    }
}