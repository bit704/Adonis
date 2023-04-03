package com.example.adonis.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import com.example.adonis.R
import com.example.adonis.services.WebSocketService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("Activity Info", "Login Create")

        val textUser:EditText = findViewById(R.id.edit_text_user)
        val textPassword:EditText = findViewById(R.id.edit_text_password)
        val buttonLogin:Button = findViewById(R.id.button_login)
        val buttonSignup:Button = findViewById(R.id.button_signup)

        val onclick = OnClick()
        buttonLogin.setOnClickListener(onclick)
        buttonSignup.setOnClickListener(onclick)

        val loginConnection = LoginConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, loginConnection, BIND_AUTO_CREATE)
    }

    inner class OnClick:OnClickListener{
        override fun onClick(view: View?) {
            val intent = Intent()
            when(view?.id){
                R.id.button_login -> {
                    intent.setClass(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.button_signup -> {
                    intent.setClass(this@LoginActivity, SignupActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    inner class LoginConnection: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.i("Activity Info", "Login Connect")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Login DisConnect")
        }

    }

}