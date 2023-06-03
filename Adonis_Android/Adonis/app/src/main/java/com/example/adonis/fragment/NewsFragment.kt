package com.example.adonis.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.activity.AddActivity
import com.example.adonis.activity.MainActivity
import com.example.adonis.activity.SingleChatActivity
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.utils.NewsAdapter

class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var onlineTag: Boolean = true
    private val newsAdapter = NewsAdapter()
    private lateinit var data: AdonisApplication

    private var isChecking = false

    private val timeHandler = TimeHandler()
    private val timeThread = TimeThread()

    private var onlineText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            onlineTag = it.getBoolean(FilterString.ONLINE_STATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        data = activity?.application as AdonisApplication

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_news)
        val moreButton = view.findViewById<Button>(R.id.button_news_more)
        onlineText = view.findViewById(R.id.text_online_state)

        updateOnlineState(onlineTag)

        val layout = LinearLayoutManager(view.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout

        newsAdapter.setItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick(holder: NewsAdapter.NewsHolder) {
                val intent = Intent(activity, SingleChatActivity::class.java)
                intent.putExtra(FilterString.ID, holder.id)
                intent.putExtra(FilterString.NICKNAME, holder.nickname.text)
                newsAdapter.clearUnread(holder.id!!)
                val mainActivity = activity as MainActivity
                mainActivity.updateUnread(holder.id!!)
                startActivity(intent)
            }

            override fun onItemLongClick() {

            }
        })
        recyclerView.adapter = newsAdapter

        moreButton.setOnClickListener{
            val popupMenu = PopupMenu(this.context, moreButton)
            popupMenu.menuInflater.inflate(R.menu.menu_main_more, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_more_add -> {
                        val intent = Intent(activity, AddActivity::class.java)
                        startActivity(intent)
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }


//        val onGesture = OnGesture()
//        gestureDetector = GestureDetector(recyclerView.context, onGesture)
//        recyclerView.setOnTouchListener(this)
//        recyclerView.isLongClickable = true

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(online: Boolean) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(FilterString.ONLINE_STATE, online)
                }
            }
    }



    fun initNewsList() {
        newsAdapter.initNewsList(data)
        if (!isChecking)
            timeThread.start()
    }

    fun addNewsList(news: DialogueMessage, tag: Boolean) {
        newsAdapter.addNewsList(news, tag)
    }

    fun clearUnread(id: String) {
        newsAdapter.clearUnread(id)
    }

    fun deleteNews(id: String) {
        newsAdapter.deleteNews(id)
    }

    fun updateOnlineState(tag: Boolean) {
        if (tag) {
            onlineText!!.visibility = ViewGroup.GONE
        } else {
            onlineText!!.visibility = ViewGroup.VISIBLE
        }
    }

    @SuppressLint("HandlerLeak")
    inner class TimeHandler: Handler() {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            when(msg.what) {
                1 -> newsAdapter.updateNewsList()
            }
        }
    }

    inner class TimeThread: Thread() {
        override fun run() {
            super.run()
            isChecking = true
            while (isChecking) {
                sleep(1000)
                timeHandler.sendEmptyMessage(1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isChecking = false
    }

}