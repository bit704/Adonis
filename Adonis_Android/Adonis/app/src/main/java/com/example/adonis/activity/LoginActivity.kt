package com.example.adonis.activity

import android.content.*
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import java.util.*


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    private val REQUEST_CODE = 1
    private val RESULT_CODE = 2

    private lateinit var myID: String
    private lateinit var myPWD: String
    private var mWindowHeight = 0
    private var globalLayoutListener: OnGlobalLayoutListener? = null
    private var getHeight: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("Activity Info", "Login Create")

        if (intent.getBooleanExtra(FilterString.AUTO_LOGIN, false)) {
            Toast.makeText(this, "账号信息变更，请重新登录！", Toast.LENGTH_SHORT).show()
        }

        val loginConnection = LoginConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, loginConnection, BIND_AUTO_CREATE)

        val textUser:EditText = findViewById(R.id.edit_text_user)
        val textPassword:EditText = findViewById(R.id.edit_text_password)
        val buttonLogin:Button = findViewById(R.id.button_login)
        val buttonSignup:Button = findViewById(R.id.button_signup)

        val data = application as AdonisApplication

        buttonLogin.setOnClickListener {
            myID = textUser.text.toString()
            myPWD = textPassword.text.toString()

            hideKeyBoards()
            if (myID.isEmpty() && myPWD.isEmpty()) {
                Toast.makeText(this, "请输入账号和密码！", Toast.LENGTH_SHORT).show()
                showKeyBoards(textUser)
                return@setOnClickListener
            }
            if (myID.isEmpty()) {
                Toast.makeText(this, "请输入账号！", Toast.LENGTH_SHORT).show()
                showKeyBoards(textUser)
                return@setOnClickListener
            }
            if (myPWD.isEmpty()) {
                Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show()
                showKeyBoards(textPassword)
                return@setOnClickListener
            }

            buttonLogin.isClickable = false

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

        buttonSignup.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@LoginActivity, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        textUser.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_NEXT) {
                showKeyBoards(textPassword)
            }
            false
        }

        textPassword.setOnEditorActionListener{ _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                buttonLogin.callOnClick()
            }
            false
        }


        intentFilter.addAction(FilterString.USER_INFO_MESSAGE)
        intentFilter.addAction(FilterString.OFF_LINE)
        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action) {
                    FilterString.OFF_LINE -> {
                        Toast.makeText(this@LoginActivity, "网络异常, 请稍后重试！", Toast.LENGTH_SHORT).show()
                    }
                    FilterString.USER_INFO_MESSAGE -> {
                        val code = p1.getIntExtra(FilterString.CODE, Code.DEFAULT_CODE)
                        if (code == 200) {
                            val editor =
                                getSharedPreferences(FilterString.DATA, MODE_PRIVATE).edit()
                            editor.putString(FilterString.ID, myID)
                            editor.putString(FilterString.PASSWORD, myPWD)
                            editor.putBoolean(FilterString.IF_ONLINE, true)
                            editor.apply()
                            data.initMyID(myID)
                            service.initMyID(myID)
                            val intent = Intent()
                            intent.setClass(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra(FilterString.ONLINE_STATE, true)
                            startActivity(intent)
                            finish()
                        } else {
                            buttonLogin.isClickable = true
                            Toast.makeText(this@LoginActivity, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        globalLayoutListener = OnGlobalLayoutListener {
            if (getHeight) return@OnGlobalLayoutListener
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            val height: Int = r.height()
            if (mWindowHeight == 0) {
                mWindowHeight = height
            } else {
                if (mWindowHeight != height) {
                    val softKeyboardHeight: Int = mWindowHeight - height
                    val editor = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).edit()
                    editor.putInt(FilterString.SOFT_INPUT_HEIGHT, softKeyboardHeight)
                    editor.apply()
                    getHeight = true
                }
            }
        }

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> if (resultCode == RESULT_CODE){
                val result = data?.getIntExtra(FilterString.RESULT, Code.DEFAULT_CODE)
                if (result == Code.SUCCESS) {
                    Toast.makeText(this, "注册成功，请登录！", Toast.LENGTH_SHORT).show()
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }
}