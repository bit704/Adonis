package com.example.adonis.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.DialogueMessage
import java.util.concurrent.CopyOnWriteArrayList

class ChatAdapter: Adapter<ViewHolder>() {
    private var messageList = CopyOnWriteArrayList<DialogueMessage>()
    private var lastedPosition = CopyOnWriteArrayList<Int>()
    private var mineId: String? = null


    private val MINE = 1
    private val OTHERS = 2

    inner class SelfHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.chat_mine_avatar)
        val msg: TextView = itemView.findViewById(R.id.text_chat_mine)
        val time: TextView = itemView.findViewById(R.id.text_chat_mine_time)
        val lastedTime: TextView = itemView.findViewById(R.id.text_chat_mine_lasted_time)
    }

    inner class OthersHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.chat_others_avatar)
        val msg: TextView = itemView.findViewById(R.id.text_chat_others)
        val time: TextView = itemView.findViewById(R.id.text_chat_others_time)
        val lastedTime: TextView = itemView.findViewById(R.id.text_chat_others_lasted_time)
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == MINE) {
            if (holder is SelfHolder) {
                holder.msg.text = messageList[position].content.toString()
                setLastTime(holder, position)
                setTime(holder, position)
            }
        } else {
            if (holder is OthersHolder) {
                holder.msg.text = messageList[position].content.toString()
                setLastTime(holder, position)
                setTime(holder, position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when(payloads[0]) {
                "1" -> setLastTime(holder, position)
                "2" -> {
                    setTime(holder, position)
                    setLastTime(holder, position)
                }
            }
        }
    }

    private fun setLastTime(holder:ViewHolder, position: Int) {
        val lasted = messageList[position].lastedTime
        var time: String? = null
        if (lasted.toInt() != 0) {
            val timeResult = AdonisFunction.longToTime(lasted)
            val hours = if (timeResult.hours > 0) {
                timeResult.hours.toString() + "h"
            } else {
                ""
            }
            val minutes = if (timeResult.minutes > 0) {
                timeResult.minutes.toString() + "'"
            } else {
                ""
            }
            val seconds = if (timeResult.seconds > 0) {
                timeResult.seconds.toString() + "''"
            } else {
                ""
            }
            time = hours + minutes + seconds
        }
        if (getItemViewType(position) == MINE) {
            if (holder is SelfHolder) {
                if (time.isNullOrEmpty()) {
                    holder.lastedTime.visibility = ViewGroup.GONE
                } else {
                    holder.lastedTime.text = time
                    holder.lastedTime.visibility = ViewGroup.VISIBLE
                }
            }
        } else {
            if (holder is OthersHolder) {
                if (time.isNullOrEmpty()) {
                    holder.lastedTime.visibility = ViewGroup.GONE
                } else {
                    holder.lastedTime.text = time
                    holder.lastedTime.visibility = ViewGroup.VISIBLE
                }
            }
        }
    }

    private fun setTime(holder: ViewHolder, position: Int) {
        val occurred = messageList[position].occurredTime
        if (getItemViewType(position) == MINE) {
            if (holder is SelfHolder) {
                if (ifShowTime(position)) {
                    holder.time.text = AdonisFunction.longToStringDate(occurred)
                    holder.time.visibility = ViewGroup.VISIBLE
                } else {
                    holder.time.visibility = ViewGroup.GONE
                }
            }
        } else {
            if (holder is OthersHolder) {
                if (ifShowTime(position)) {
                    holder.time.text = AdonisFunction.longToStringDate(occurred)
                    holder.time.visibility = ViewGroup.VISIBLE
                } else {
                    holder.time.visibility = ViewGroup.GONE
                }
            }
        }
    }

    private fun ifShowTime(position: Int): Boolean {
        return if (position > 0) {
            (messageList[position].occurredTime - messageList[position - 1].occurredTime) > 3 * 1000 * 60
        } else {
            true
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

    @SuppressLint("NotifyDataSetChanged")
    fun initChatList(result: FindChatResult, id: String?) {
        this.messageList = result.result
        this.lastedPosition = result.position
        mineId = id
        notifyDataSetChanged()
    }

    fun addChatList(msg: DialogueMessage) {
        this.messageList.add(msg)
        if(msg.lastedTime > 0)
            lastedPosition.add(itemCount-1)
        notifyItemInserted(itemCount-1)
    }

    fun updateLastedNews(): Boolean {
        for (i in 0 until lastedPosition.size) {
            val pos = lastedPosition[i]
            if (pos==-1) continue
            val lastTime = messageList[pos].lastedTime
            messageList[pos].lastedTime = lastTime - 1000
            notifyItemChanged(pos, "1")
            if (lastTime <= 1000) {
                for (j in i until lastedPosition.size) {
                    lastedPosition[j] -= 1
                }
                lastedPosition[i] = -1
                messageList.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemChanged(pos, "2")
            }
        }
        lastedPosition.remove(-1)
        return lastedPosition.size == 0
    }

}