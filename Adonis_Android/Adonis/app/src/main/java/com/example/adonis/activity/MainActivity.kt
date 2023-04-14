package com.example.adonis.activity


import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import android.view.View

import android.widget.FrameLayout
import android.widget.ImageButton

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.entity.ActionString
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.Message
import com.example.adonis.entity.MessageCode
import com.example.adonis.entity.UserOnlineMessage
import com.example.adonis.entity.UserOpMessage
import com.example.adonis.fragment.ContactsFragment
import com.example.adonis.fragment.NewsFragment
import com.example.adonis.fragment.PersonalFragment
import com.example.adonis.services.WebSocketService
import java.util.UUID


class MainActivity : AppCompatActivity(){
    lateinit var binder: WebSocketService.SocketBinder
    lateinit var service: WebSocketService
    lateinit var receiver: BroadcastReceiver
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
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = getSharedPreferences(FilterString.DATA, MODE_PRIVATE)
        userId = pref.getString(FilterString.ID, null).toString()

        val newsButton:ImageButton = findViewById(R.id.button_news)
        val contactsButton:ImageButton = findViewById(R.id.button_contacts)
        val personalButton:ImageButton = findViewById(R.id.button_personal)
        val frameLayout: FrameLayout = findViewById(R.id.frame_main)

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

        intentFilter.addAction(FilterString.USER_ONLINE_MESSAGE)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.i("Receiver", "received")
                when(p1?.action) {
                    FilterString.USER_ONLINE_MESSAGE -> {
                        val initInfo = p1.getStringExtra(FilterString.USER_ONLINE_MESSAGE)
                        val userOnlineMessage = JSON.parseObject(initInfo, UserOnlineMessage::class.java)
                        contactsFragment?.initContacts(userOnlineMessage.friendInfoMessageList)
                        newsFragment?.initNewsList(userOnlineMessage.dialogueInfoMessageList)
                    }
                }
            }
        }

        val click = OnClick()
        newsButton.setOnClickListener(click)
        contactsButton.setOnClickListener(click)
        personalButton.setOnClickListener(click)

        val mainConnection = MainConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, mainConnection, BIND_AUTO_CREATE)
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
        newsFragment = NewsFragment.newInstance("1", "1")
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
            val msg = Message()
            val userOp = UserOpMessage()
            msg.id = UUID.randomUUID().toString()
            msg.type = FilterString.USER_OP_MESSAGE
            userOp.code = MessageCode.uop_request_online_message.id
            userOp.id = userId
            msg.userOpMessage = userOp
            val request = JSON.toJSONString(msg)
            service.sendMessage(request, msg.id, MessageCode.uop_request_online_message.id)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Main DisConnect")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("Receiver", "register")
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        Log.i("Receiver", "unregister")
        unregisterReceiver(receiver)
    }
}