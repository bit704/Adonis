package com.example.adonis.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ViewHolder>()  {

    var onItemClickListener: OnItemClickListener? = null
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val avatar: ImageView = view.findViewById(R.id.news_avatar)
        val nickname: TextView = view.findViewById(R.id.news_name)
        val message: TextView = view.findViewById(R.id.news_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_news, null)
        return ViewHolder(newsView)
    }

    override fun getItemCount(): Int {
        return 14
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null){
                onItemClickListener?.onItemClick()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun setItemClickListener(listener:OnItemClickListener){
        this.onItemClickListener = listener
    }

    interface OnItemClickListener{
        fun onItemClick()
        fun onItemLongClick()
    }

}