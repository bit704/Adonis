package com.example.adonis.activity


import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import android.view.View

import android.widget.ImageButton

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.*
import com.example.adonis.fragment.ContactsFragment
import com.example.adonis.fragment.NewsFragment
import com.example.adonis.fragment.PersonalFragment
import com.example.adonis.services.WebSocketService
import java.util.UUID


class MainActivity : AppCompatActivity(){
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()

    private val fragmentManager = supportFragmentManager
    private var newsFragment: NewsFragment? = null
    private var contactsFragment: ContactsFragment? = null
    private var personalFragment: PersonalFragment? = null

    private lateinit var drawerLayout: DrawerLayout

    private val newsFragmentKey:String = "newsFragment"
    private val contactsFragmentKey:String = "contactsFragment"
    private val personalFragmentKey:String = "personalFragment"

    private val fragmentList = mutableListOf<Fragment>()
    private var userId: String? = null
    private var isInitData: Boolean = false
    private var backFromChat: Boolean = false
    private var onlineTag = true

    private lateinit var data: AdonisApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data = application as AdonisApplication
        userId = data.getMyID()

        onlineTag = intent.getBooleanExtra(FilterString.ONLINE_STATE, true)

        val mainConnection = MainConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, mainConnection, BIND_AUTO_CREATE)

        val newsButton:ImageButton = findViewById(R.id.button_news)
        val contactsButton:ImageButton = findViewById(R.id.button_contacts)
        val personalButton:ImageButton = findViewById(R.id.button_personal)

        drawerLayout = findViewById(R.id.main_drawer)

        if (savedInstanceState == null) {
            initFragment()
        } else{
            newsFragment = fragmentManager.getFragment(savedInstanceState, newsFragmentKey) as NewsFragment
            contactsFragment = fragmentManager.getFragment(savedInstanceState, contactsFragmentKey) as ContactsFragment
            personalFragment = fragmentManager.getFragment(savedInstanceState, personalFragmentKey) as PersonalFragment

            addToList(newsFragment)
            addToList(contactsFragment)
            addToList(personalFragment)
        }

        intentFilter.addAction(FilterString.UPDATE_UNREAD_NEWS)
        intentFilter.addAction(FilterString.USER_INFO_MESSAGE)
        intentFilter.addAction(FilterString.UPDATE_NEWS)
        intentFilter.addAction(FilterString.USER_ONLINE_MESSAGE)
        intentFilter.addAction(FilterString.FRIEND_INFO_MESSAGE)
        intentFilter.addAction(FilterString.DIALOGUE_INFO_MESSAGE)
        intentFilter.addAction(FilterString.ONLINE_STATE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.i("Receiver", "received")
                when(p1?.action) {
                    FilterString.ONLINE_STATE -> {
                        val tag = p1.getBooleanExtra(FilterString.ONLINE_STATE, true)
                        newsFragment!!.updateOnlineState(tag)
                        if (tag) {
                            reLoginIn()
                        }
                    }
                    FilterString.USER_ONLINE_MESSAGE -> {
                        Thread {
                            runOnUiThread {
                                while (true) {
                                    if (isInitData) break
                                }
                                val initInfo = p1.getStringExtra(FilterString.USER_ONLINE_MESSAGE)
                                val userOnlineMessage =
                                    JSON.parseObject(initInfo, UserOnlineMessage::class.java)
                                data.initInfo(
                                    userOnlineMessage.friendInfoMessageList,
                                    userOnlineMessage.dialogueMessageList
                                )
                                contactsFragment?.initContacts()
                                newsFragment?.initNewsList()
                                personalFragment?.initInfo()
                            }
                        }.start()
                    }
                    FilterString.FRIEND_INFO_MESSAGE -> {
                        val jsonInfo = p1.getStringExtra(FilterString.FRIEND_INFO_MESSAGE)
                        val type = p1.getIntExtra(FilterString.TYPE, Code.DEFAULT_CODE)
                        val friendInfo = JSON.parseObject(jsonInfo, FriendInfoMessage::class.java)
                        when (friendInfo.code) {
                            MessageCode.FIF_OP_SUCCESS.id -> {
                                when (type) {
                                    MessageCode.FOP_ADD.id -> {
                                        friendInfo.code = MessageCode.FIF_ADD_TO.id
                                        data.updateNewFriendsList(friendInfo)
                                    }
                                    MessageCode.FOP_CONSENT.id -> {
                                        contactsFragment?.addContacts(friendInfo)
                                    }

                                    MessageCode.FOP_DELETE.id -> {
                                        contactsFragment?.deleteContacts(friendInfo.id)
                                        newsFragment?.deleteNews(friendInfo.id)
                                        data.deleteNews(friendInfo.id)
                                    }
                                    MessageCode.FOP_CUSTOM_NICKNAME.id -> {
                                        contactsFragment?.updateContact(friendInfo)

                                    }
                                }
                            }

                        }
                    }
                    FilterString.DIALOGUE_INFO_MESSAGE -> {
                        val jsonMessage = p1.getStringExtra(FilterString.DIALOGUE_INFO_MESSAGE)
                        val msg = JSON.parseObject(jsonMessage, DialogueMessage::class.java)
                        val tag = msg.receiverId != msg.senderId
                        newsFragment?.addNewsList(msg, tag)
                    }
                    FilterString.UPDATE_NEWS -> {
                        val jsonMessage = p1.getStringExtra(FilterString.DIALOGUE_INFO_MESSAGE)
                        val msg = JSON.parseObject(jsonMessage, DialogueMessage::class.java)
                        newsFragment?.addNewsList(msg, false)
                    }
                    FilterString.UPDATE_UNREAD_NEWS -> {
                        val targetID = p1.getStringExtra(FilterString.ID)
                        backFromChat = true
                        newsFragment?.clearUnread(targetID!!)
                        service.updateUnread(targetID!!)
                    }
                    FilterString.USER_INFO_MESSAGE -> {
                        when(p1.getIntExtra(FilterString.CODE, Code.DEFAULT_CODE)) {
                            MessageCode.UIF_OP_SUCCESS.id -> {
                                when(p1.getIntExtra(FilterString.TYPE, Code.DEFAULT_CODE)) {
                                    MessageCode.UOP_SIGN_OUT.id -> {
                                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        val editor = getSharedPreferences(FilterString.DATA, MODE_PRIVATE).edit()
                                        editor.putBoolean(FilterString.IF_ONLINE, false)
                                        editor.apply()
                                        data.exit()
                                        finish()
                                    }
                                    MessageCode.UOP_SIGN_IN.id -> {
                                        requestOnlineMessage()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val click = OnClick()
        newsButton.setOnClickListener(click)
        contactsButton.setOnClickListener(click)
        personalButton.setOnClickListener(click)


        Log.i("Receiver", "register")
        registerReceiver(receiver, intentFilter)
    }

    inner class OnClick: View.OnClickListener {
        override fun onClick(view: View?) {
            when(view?.id){
                R.id.button_news -> {
                    showFragment(newsFragment!!)
                }
                R.id.button_contacts -> {
                    showFragment(contactsFragment!!)
                }
                R.id.button_personal -> {
                    showFragment(personalFragment!!)
                }
            }
        }
    }


    private fun initFragment(){
        newsFragment = NewsFragment.newInstance(onlineTag)
        contactsFragment = ContactsFragment.newInstance("1", "1")
        personalFragment = PersonalFragment.newInstance("1", "1")
        addFragment(newsFragment!!)
        addFragment(contactsFragment!!)
        addFragment(personalFragment!!)
        showFragment(newsFragment!!)

    }

    private fun addFragment(fragment: Fragment){
        if (!fragment.isAdded){
            fragmentManager.beginTransaction().add(R.id.frame_main, fragment).commit()
            fragmentList.add(fragment)
        }
        //Log.i("list", fragmentList.count().toString())
    }

    private fun addToList(fragment: Fragment?){
        if (fragment != null) {
            fragmentList.add(fragment)
        }
    }

    private fun showFragment(fragment: Fragment){
        for (frag: Fragment in fragmentList){
            if (frag != fragment){
                fragmentManager.beginTransaction().hide(frag).commit()
                //Log.i("hide", frag.toString())
            }
        }
        fragmentManager.beginTransaction().show(fragment).commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(newsFragment != null)
            fragmentManager.putFragment(outState, newsFragmentKey, newsFragment!!)
        if(contactsFragment != null)
            fragmentManager.putFragment(outState, contactsFragmentKey, contactsFragment!!)
        if(personalFragment != null)
            fragmentManager.putFragment(outState, personalFragmentKey, personalFragment!!)
        super.onSaveInstanceState(outState)
    }

    inner class MainConnection: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            binder = p1 as WebSocketService.SocketBinder
            service = binder.getService()

            Thread {
                service.initDataBase()
                data.initMyDB(service.getDataBase())
                data.initFromDB()
                isInitData = true
                service.checkLastTime()
            }.start()

            if (onlineTag) requestOnlineMessage()
            else {
                Thread {
                    runOnUiThread {
                        while (true) {
                            if (isInitData) break
                        }

                        contactsFragment?.initContacts()
                        newsFragment?.initNewsList()
                        personalFragment?.initInfo()
                    }
                }.start()
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Main DisConnect")
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Receiver", "unregister")
        unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        if (backFromChat) {
            showFragment(newsFragment!!)
            backFromChat = false
        }
    }

    fun sendExitMessage() {
        val msg = Message()
        val userOp = UserOpMessage()
        msg.id = UUID.randomUUID().toString()
        msg.type = FilterString.USER_OP_MESSAGE
        userOp.code = MessageCode.UOP_SIGN_OUT.id
        userOp.id = userId
        msg.userOpMessage = userOp
        val request = JSON.toJSONString(msg)
        service.sendMessage(request, msg.id, MessageCode.UOP_SIGN_OUT.id)
    }

    fun updateUnread(id: String) {
        service.updateUnread(id)
    }

    fun reLoginIn() {
        val sharedPreferences = getSharedPreferences(FilterString.DATA, MODE_PRIVATE)
        val myID = sharedPreferences.getString(FilterString.ID, null).toString()
        val myPWD = sharedPreferences.getString(FilterString.PASSWORD, null).toString()
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

    fun requestOnlineMessage() {
        val msg = Message()
        val userOp = UserOpMessage()
        msg.id = UUID.randomUUID().toString()
        msg.type = FilterString.USER_OP_MESSAGE
        userOp.code = MessageCode.UOP_REQUEST_ONLINE_MESSAGE.id
        userOp.id = userId
        msg.userOpMessage = userOp
        val request = JSON.toJSONString(msg)
        service.sendMessage(request, msg.id, MessageCode.UOP_REQUEST_ONLINE_MESSAGE.id)
    }
}