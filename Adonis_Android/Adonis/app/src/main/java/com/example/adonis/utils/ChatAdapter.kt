package com.example.adonis.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.DialogueMessage
import kotlin.math.min

class ChatAdapter: Adapter<ViewHolder>() {
    private var messageList = mutableListOf<DialogueMessage>()
    private var mineId: String? = null

    private val MINE = 1
    private val OTHERS = 2

    inner class SelfHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.chat_mine_avatar)
        val msg: TextView = itemView.findViewById(R.id.text_chat_mine)
    }

    inner class OthersHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.chat_others_avatar)
        val msg: TextView = itemView.findViewById(R.id.text_chat_others)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MINE) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_chat_self, null)
            SelfHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_chat_others, null)
            OthersHolder((itemView))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == MINE) {
            if (holder is SelfHolder) {
                holder.msg.text = messageList[position].content.toString()
            }
        } else {
            if (holder is OthersHolder) {
                holder.msg.text = messageList[position].content.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == mineId) {
            MINE
        } else {
            OTHERS
        }
    }

    fun initChatList(list: MutableList<DialogueMessage>?, id: String?) {
        this.messageList.addAll(list!!)
        this.mineId = id
        notifyDataSetChanged()
    }

    fun addChatList(msg: DialogueMessage) {
        this.messageList.add(msg)
        notifyItemInserted(itemCount)
        notifyItemChanged(itemCount)

    }
}