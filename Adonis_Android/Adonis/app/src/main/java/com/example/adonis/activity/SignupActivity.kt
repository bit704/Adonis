package com.example.adonis.activity

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adonis.R
import com.example.adonis.services.WebSocketService
import com.alibaba.fastjson.*
import com.example.adonis.entity.*
import java.util.UUID


class SignupActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()
    private val RESULT_CODE = 2
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
            val userOpMessage = UserOpMessage()
            val message = Message()
            val nickname = nameEditText.text.toString()
            val id = userEditText.text.toString()
            val pwd = pwdEditText.text.toString()
            val confirmPwd = confirmEditText.text.toString()
            if (nickname.isBlank()) {
                Toast.makeText(this, "昵称不能为空！", Toast.LENGTH_SHORT).show()
                showKeyBoards(nameEditText)
                return@setOnClickListener
            }
            if (id.isEmpty()) {
                Toast.makeText(this, "账号不能为空！", Toast.LENGTH_SHORT).show()
                showKeyBoards(userEditText)
                return@setOnClickListener
            }
            if (pwd.isEmpty()) {
                Toast.makeText(this, "请设置密码！", Toast.LENGTH_SHORT).show()
                showKeyBoards(pwdEditText)
                return@setOnClickListener
            }
            if (pwd != confirmPwd) {
                Toast.makeText(this, "两次输入密码不一致！", Toast.LENGTH_SHORT).show()
                showKeyBoards(confirmEditText)
                return@setOnClickListener
            }

            userOpMessage.code = MessageCode.UOP_SIGN_UP.id
            userOpMessage.id = id
            userOpMessage.nickname = nickname
            userOpMessage.password = pwd
            message.type = FilterString.USER_OP_MESSAGE
            message.id = UUID.randomUUID().toString()
            message.userOpMessage = userOpMessage
            val msg = JSON.toJSONString(message)
            service.sendMessage(msg, message.id, MessageCode.UOP_SIGN_UP.id)

        }

        nameEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                showKeyBoards(userEditText)
            }
            false
        }

        userEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                showKeyBoards(pwdEditText)
            }
            false
        }

        pwdEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                showKeyBoards(confirmButton)
            }
            false
        }

        intentFilter.addAction(FilterString.USER_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@SignupActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT)
                            .show()
                    }
                    FilterString.USER_INFO_MESSAGE -> {
                        val code = p1?.getIntExtra(FilterString.CODE, Code.DEFAULT_CODE)
                        if (code == 200) {
                            val data = Intent()
                            data.putExtra(FilterString.RESULT, Code.SUCCESS)
                            setResult(RESULT_CODE, data)
                            finish()
                        } else if (code == 201) {
                            val toast = Toast.makeText(
                                this@SignupActivity,
                                "账号已存在，请重新设置！",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.BOTTOM, 0, 100)
                            toast.show()
                        }
                    }
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

    inner class SignupConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Signup DisConnect")
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

}