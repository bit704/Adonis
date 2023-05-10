package com.example.adonis.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.FilterString

class UserInfoActivity : AppCompatActivity() {
    private lateinit var data: AdonisApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        data = application as AdonisApplication
        val userID = intent.getStringExtra(FilterString.ID)
        val user = data.findUserByID(userID)

        val backButton: Button = findViewById(R.id.button_user_info_back)
        val remark: TextView = findViewById(R.id.userInfo_remark)
        val nickname: TextView = findViewById(R.id.userInfo_nickname)
        val id: TextView = findViewById(R.id.userInfo_id)

        id.text = userID
        if (user?.customNickname != null) {
            remark.text = user.customNickname
            nickname.text = user.nickname
        } else {
            remark.text = user?.nickname
        }

        val chatButton: Button = findViewById(R.id.button_user_info_chat)
        chatButton.setOnClickListener {
            val intent = Intent(this, SingleChatActivity::class.java)
            intent.putExtra(FilterString.ID, userID)
            intent.putExtra(FilterString.NICKNAME, user?.nickname)
            intent.putExtra(FilterString.REMARK, user?.customNickname)
            startActivity(intent)
        }

        backButton.setOnClickListener { finish() }
    }
}