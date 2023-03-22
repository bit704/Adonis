package com.example.adonis.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.adonis.R
import com.example.adonis.services.WebSocketService

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val intent = Intent(this, WebSocketService::class.java)

        startService(intent)

        val handler = Handler()
        handler.postDelayed({
            val it = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(it)
            this@WelcomeActivity.finish()
        }, 1000*3)
    }

}