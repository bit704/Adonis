package com.example.adonis.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*
import com.example.adonis.services.WebSocketService
import java.util.*

class UserInfoActivity : AppCompatActivity() {
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService

    private lateinit var data: AdonisApplication

    private lateinit var remark: TextView
    private lateinit var nickname: TextView
    private lateinit var chatButton: Button
    private lateinit var editButton: Button
    private lateinit var user: FriendInfoMessage

    private var nickName: String? = null
    private var customNickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        data = application as AdonisApplication
        val jsonString = intent.getStringExtra(FilterString.DATA)
        user = JSON.parseObject(jsonString, FriendInfoMessage::class.java)
        val userID = user.id

        val backButton: Button = findViewById(R.id.button_user_info_back)
        remark = findViewById(R.id.userInfo_remark)
        nickname = findViewById(R.id.userInfo_nickname)
        val id: TextView = findViewById(R.id.userInfo_id)
        chatButton = findViewById(R.id.button_user_info_chat)
        editButton = findViewById(R.id.button_user_info_edit)

        id.text = userID

        val tag = data.findUserByID(user.id)
        if (tag == null) {
            remark.text = user.nickname
            chatButton.text = FilterString.ADD_FRIEND
            chatButton.setOnClickListener {
                val intent = Intent(this, ConfirmActivity::class.java)
                intent.putExtra(FilterString.DATA, jsonString)
                startActivity(intent)
            }
        } else {
            nickName= user.nickname

            if (!user.customNickname.isNullOrEmpty()) {
                remark.text = user.customNickname
                nickname.text = user.nickname
                customNickname = user.customNickname
            } else {
                remark.text = user.nickname
                customNickname = ""
            }

            chatButton.setOnClickListener {
                val intent = Intent(this, SingleChatActivity::class.java)
                intent.putExtra(FilterString.ID, userID)
                intent.putExtra(FilterString.NICKNAME, user.nickname)
                intent.putExtra(FilterString.REMARK, user.customNickname)

                val updateUnread = Intent(FilterString.UPDATE_UNREAD_NEWS)
                updateUnread.putExtra(FilterString.ID, userID)
                sendBroadcast(updateUnread)
                startActivity(intent)
                finish()
            }

            if (userID == data.getMyID()) {
                editButton.visibility = ViewGroup.GONE
            } else {
                editButton.setOnClickListener {
                    val intent = Intent(this, EditUserInfoActivity::class.java)
                    intent.putExtra(FilterString.ID, userID)
                    intent.putExtra(FilterString.CUSTOM_NICKNAME, customNickname)
                    intent.putExtra(FilterString.NICKNAME, nickName)
                    startActivityForResult(intent, Code.REQUEST_CODE)
                }
            }

        }

        backButton.setOnClickListener { finish() }
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.appcompat.app.AppCompatActivity"
    )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Code.REQUEST_CODE) {
            if (resultCode == Code.DELETE_RESULT_CODE) {
                remark.text = user.nickname
                nickname.text = ""
                editButton.visibility = ViewGroup.GONE
                chatButton.text = FilterString.ADD_FRIEND
                chatButton.setOnClickListener {
                    val intent = Intent(this, ConfirmActivity::class.java)
                    intent.putExtra(FilterString.DATA, JSON.toJSONString(user))
                    startActivity(intent)
                }
            }
            if (resultCode == Code.NICKNAME_RESULT_CODE) {
                user = JSON.parseObject(data!!.getStringExtra(FilterString.DATA), FriendInfoMessage::class.java)
                if (!user.customNickname.isNullOrBlank()) {
                    remark.text = user.customNickname
                    nickname.text = user.nickname
                    customNickname = user.customNickname
                } else {
                    remark.text = user.nickname
                    nickname.text = ""
                    customNickname = ""
                }
            }
        }
    }

}