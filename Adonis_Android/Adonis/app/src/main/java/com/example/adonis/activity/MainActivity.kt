package com.example.adonis.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View

import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.adonis.R
import com.example.adonis.fragment.ContactsFragment
import com.example.adonis.fragment.NewsFragment


class MainActivity : AppCompatActivity(){
    private val fragmentManager = supportFragmentManager
    private var newsFragment: NewsFragment? = null
    private var contactsFragment: ContactsFragment? = null

    private lateinit var drawerLayout: DrawerLayout

    private val newsFragmentKey:String = "newsFragment"
    private val contactsFragmentKey:String = "contactsFragment"

    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val click = OnClick()
        newsButton.setOnClickListener(click)
        contactsButton.setOnClickListener(click)
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

    inner class OnLeftSwipe: NewsFragment.OnLeftSwipeListener{
        override fun onLeftSwipe() {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initFragment(){
        newsFragment = NewsFragment.newInstance("1", "1")
        val onLeftSwipe = OnLeftSwipe()
        newsFragment?.setLeftSwipeListener(onLeftSwipe)
        addFragment(newsFragment!!)
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


}