package com.example.adonis.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage
import java.util.concurrent.CopyOnWriteArrayList

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsHolder>()  {


    private var newsList = mutableMapOf<String, CopyOnWriteArrayList<DialogueMessage>>()
    private var orderList = mutableListOf<String>()
    private var unReadNewsListData = mutableMapOf<String, MutableList<DialogueMessage>>()
    private var data: AdonisApplication? = null

    var onItemClickListener: OnItemClickListener? = null
    inner class NewsHolder(view: View): RecyclerView.ViewHolder(view){
        val avatar: ImageView = view.findViewById(R.id.news_avatar)
        val nickname: TextView = view.findViewById(R.id.news_name)
        val message: TextView = view.findViewById(R.id.news_message)
        val time: TextView = view.findViewById(R.id.text_news_time)
        val unread: TextView = view.findViewById(R.id.text_news_unread_count)
        var count: Int = 0
        var id: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_news, null)
        return NewsHolder(newsView)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val name = data?.findUserByID(orderList[position])?.customNickname
        if (!name.isNullOrBlank()) {
            holder.nickname.text =
                data?.findUserByID(orderList[position])?.customNickname.toString()
        } else {
            holder.nickname.text =
                data?.findUserByID(orderList[position])?.nickname.toString()
        }
        setText(holder, position)
        holder.id = orderList[position]
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null){
                onItemClickListener?.onItemClick(holder)
            }
        }

        setUnread(holder, position)
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when(payloads[0]) {
                "1" -> {
                    holder.count = 0
                    holder.unread.text = ""
                    setText(holder, position)
                }
                "2" -> setText(holder, position)
            }
        }
    }

    private fun setText(holder: NewsHolder, position: Int) {
        val id = orderList[position]
        val msg =  if (unReadNewsListData[id]!!.isNotEmpty()) {
//            Log.i("Debug_Set", "here")
            unReadNewsListData[id]!!.last()
        } else {
            newsList[id]!!.last()
        }
//        Log.i("Debug_Set", msg.toString())
        if (msg.lastedTime > 0)
            holder.message.text = FilterString.LASTED_MESSAGE
        else
            holder.message.text = msg.content
        holder.time.text =
            AdonisFunction.longToStringDate(msg.occurredTime)
    }

    private fun setUnread(holder: NewsHolder, position: Int) {
        setText(holder, position)
        holder.count = unReadNewsListData[orderList[position]]!!.size
        holder.unread.text = if (holder.count == 0) { "" } else { holder.count.toString() }
    }

    fun setItemClickListener(listener:OnItemClickListener){
        this.onItemClickListener = listener
    }

    interface OnItemClickListener{
        fun onItemClick(holder: NewsHolder)
        fun onItemLongClick()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initNewsList(data: AdonisApplication) {
        this.newsList = data.getLatestNewsData()
        this.orderList = data.getOrderData()
        this.unReadNewsListData = data.getUnReadNewsListData()
        this.data = data
        notifyDataSetChanged()

    }

    fun addNewsList(news: DialogueMessage, tag: Boolean) {
        val id: String = if (news.senderId != data?.getMyID()) {
            news.senderId
        } else {
            news.receiverId
        }
        if (!unReadNewsListData.containsKey(id)) {unReadNewsListData[id] = mutableListOf() }
        if (tag) {
            unReadNewsListData[id]!!.add(news)
        }
        if (newsList.contains(id)) {
            newsList[id]!!.add(news)
            val index = orderList.indexOf(id)
            orderList.remove(id)
            orderList.add(0, id)
            notifyItemMoved(index, 0)
            notifyItemChanged(0)
        } else {
            newsList[id] = CopyOnWriteArrayList<DialogueMessage>()
            newsList[id]!!.add(news)
            orderList.add(0, id)
            notifyItemInserted(0)
        }
    }

    fun clearUnread(id: String) {
        val pos = orderList.indexOf(id)
        if (pos != -1) {
            for (msg in unReadNewsListData[id]!!) {
                if (msg.lastedTime > 0) {
                    newsList[id]?.add(msg)
                } else {
                    newsList[id]?.clear()
                    newsList[id]?.add(msg)
                }
            }
            unReadNewsListData[id] = mutableListOf()
            notifyItemChanged(pos, "1")
        }
    }

    fun deleteNews(id: String) {
        val pos = orderList.indexOf(id)
        if (pos != -1) {
            newsList.remove(id)
            orderList.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun updateNewsList(): Boolean {
        var tag = true
        val delete = mutableListOf<String>()

        for ((key, value ) in newsList) {
            val pos = orderList.indexOf(key)
            for (msg in value) {
                if (msg.lastedTime > 0) {
                    msg.lastedTime -= 1001
                    tag = false
                }
                if (msg.lastedTime < 0) {
                    if (msg === value.last()) {
                        value.remove(msg)
//                        Log.i("Debug", msg.toString())
                        notifyItemChanged(pos, "2")
                    } else {
                        value.remove(msg)
                    }
                }
            }
            if (value.size == 0) {
                delete.add(key)
            }
        }
        for (id in delete) {
            if (unReadNewsListData.containsKey(id))
                continue
            val pos = orderList.indexOf(id)
            orderList.remove(id)
            newsList.remove(id)
            notifyItemRemoved(pos)
        }
        return tag
    }
}