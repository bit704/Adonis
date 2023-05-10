package com.example.adonis.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.DialogueMessage

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsHolder>()  {


    private var newsList = mutableMapOf<String, DialogueMessage>()
    private var orderList = mutableListOf<String>()
    private var data: AdonisApplication? = null

    var onItemClickListener: OnItemClickListener? = null
    inner class NewsHolder(view: View): RecyclerView.ViewHolder(view){
        val avatar: ImageView = view.findViewById(R.id.news_avatar)
        val nickname: TextView = view.findViewById(R.id.news_name)
        val message: TextView = view.findViewById(R.id.news_message)
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
        if (name != null) {
            holder.nickname.text =
                data?.findUserByID(orderList[position])?.customNickname.toString()
        } else {
            holder.nickname.text =
                data?.findUserByID(orderList[position])?.nickname.toString()
        }
        holder.message.text = newsList[orderList[position]]?.content
        holder.id = orderList[position]
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null){
                onItemClickListener?.onItemClick(holder)
            }
        }

    }

    fun setItemClickListener(listener:OnItemClickListener){
        this.onItemClickListener = listener
    }

    interface OnItemClickListener{
        fun onItemClick(holder: NewsHolder)
        fun onItemLongClick()
    }

    fun initNewsList(data: AdonisApplication) {
        this.newsList = data.getLatestNewsData()
        this.orderList = data.getOrderData()
        this.data = data
    }

    fun addNewsList(news: DialogueMessage) {
        var id: String? = null
        id = if (news.senderId != data?.getMyID()) {
            news.senderId
        } else {
            news.receiverId
        }
        if (newsList.contains(id)) {
            newsList[id] = news
            val index = orderList.indexOf(id)
            orderList.remove(id)
            orderList.add(0, id)
            notifyItemMoved(index, 0)
            notifyItemChanged(0)
        } else {
            newsList[id] = news
            orderList.add(0, id)
            notifyItemInserted(0)
        }
    }

}