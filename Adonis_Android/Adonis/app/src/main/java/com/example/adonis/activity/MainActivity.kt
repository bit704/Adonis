package com.example.adonis.activity


import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity

import android.view.View

import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.entity.ActionString
import com.example.adonis.entity.Code
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.Message
import com.example.adonis.entity.UserOnlineMessage
import com.example.adonis.entity.UserOpMessage
import com.example.adonis.fragment.ContactsFragment
import com.example.adonis.fragment.NewsFragment
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

    private lateinit var drawerLayout: DrawerLayout

    private val newsFragmentKey:String = "newsFragment"
    private val contactsFragmentKey:String = "contactsFragment"

    private val fragmentList = mutableListOf<Fragment>()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = getSharedPreferences(FilterString.DATA, MODE_PRIVATE)
        userId = pref.getString("id", null).toString()

        val newsButton:ImageButton = findViewById(R.id.button_news)
        val contactsButton:ImageButton = findViewById(R.id.button_contacts)
        val frameLayout: FrameLayout = findViewById(R.id.frame_main)

        drawerLayout = findViewById(R.id.main_drawer)


        if (savedInstanceState == null) {
            initFragment()
        } else{
            newsFragment = fragmentManager.getFragment(savedInstanceState, newsFragmentKey) as NewsFragment
            contactsFragment = fragmentManager.getFragment(savedInstanceState, contactsFragmentKey) as ContactsFragment

            addToList(newsFragment)
            addToList(contactsFragment)
        }

        intentFilter.addAction(ActionString.MAIN_INFO)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.getStringExtra("type")) {
                    FilterString.USER_ONLINE_MESSAGE -> {
                        val initInfo = p1.getStringExtra(FilterString.USER_ONLINE_MESSAGE)
                        val userOnlineMessage = JSON.parseObject(initInfo, UserOnlineMessage::class.java)
                        contactsFragment?.initContacts(userOnlineMessage.friendInfoMessageList)
                        Log.i("contacts", contactsFragment.toString())
                    }
                }
            }
        }

        val click = OnClick()
        newsButton.setOnClickListener(click)
        contactsButton.setOnClickListener(click)

        val mainConnection = MainConnection()
        val serviceIntent = Intent(this, WebSocketService::class.java)
        bindService(serviceIntent, mainConnection, BIND_AUTO_CREATE)
    }

    inner class OnClick: View.OnClickListener {
        override fun onClick(view: View?) {
            when(view?.id){
                R.id.button_news -> {
                    if (newsFragment == null) {
                        newsFragment = NewsFragment.newInstance("1", "1")
                    }
                    addFragment(newsFragment!!)
                    showFragment(newsFragment!!)
                }
                R.id.button_contacts -> {
                    if (contactsFragment == null) {
                        contactsFragment = ContactsFragment.newInstance("1", "1")
                    }
                    addFragment(contactsFragment!!)
                    showFragment(contactsFragment!!)
                }
            }
        }
    }


    private fun initFragment(){
        newsFragment = NewsFragment.newInstance("1", "1")
        contactsFragment = ContactsFragment.newInstance("1", "1")
        addFragment(newsFragment!!)
        addFragment(contactsFragment!!)
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
            userOp.type = ActionString.REQUEST
            userOp.id = userId
            msg.userOpMessage = userOp
            val request = JSON.toJSONString(msg)
            service.sendMessage(request, msg.id, userOp.type)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("Activity Info", "Main DisConnect")
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