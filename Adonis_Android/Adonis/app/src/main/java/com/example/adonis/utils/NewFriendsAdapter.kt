package com.example.adonis.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.Code
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.MessageCode

class NewFriendsAdapter: Adapter<NewFriendsAdapter.NewFriendsViewHolder>() {
    private var list = mutableListOf<FriendInfoMessage>()
    private var agreeListener: OnAgreeButtonClickedListener? = null
    private var rejectListener: OnRejectButtonClickedListener? = null

    inner class NewFriendsViewHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.new_friends_avatar)
        val nickname: TextView = itemView.findViewById(R.id.new_friends_nickname)
        val request: TextView = itemView.findViewById(R.id.new_friends_request)
        val agree: Button = itemView.findViewById(R.id.button_new_friends)
        val reject: Button = itemView.findViewById(R.id.button_reject_friends)
        val tag: TextView = itemView.findViewById(R.id.tag_new_friends)
        var id: String? = null
        var data: FriendInfoMessage? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_new_friends, null)
        return NewFriendsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewFriendsViewHolder, position: Int) {
        holder.data = list[position]
        holder.nickname.text = list[position].nickname
        holder.request.text = list[position].memo
        holder.id = list[position].id

        holder.tag.visibility = ViewGroup.VISIBLE
        holder.agree.visibility = ViewGroup.GONE
        holder.reject.visibility = ViewGroup.GONE
        when(getItemViewType(position))  {
            Code.INVITATION-> {
                holder.tag.visibility = ViewGroup.GONE
                holder.agree.visibility = ViewGroup.VISIBLE
                holder.reject.visibility = ViewGroup.VISIBLE
                holder.agree.setOnClickListener {
                   if (agreeListener != null) {
                       agreeListener?.onAgreeButtonClick(holder, position)
                   }
                }
                holder.reject.setOnClickListener {
                    if (rejectListener != null) {
                        rejectListener?.onRejectButtonClick(holder, position)
                    }
                }
            }
            Code.REQUEST -> {
                holder.tag.text = "待通过"
            }
            Code.ADDED -> {
                holder.tag.text = "已添加"
            }
            Code.REFUSE -> {
                holder.tag.text = "已拒绝"
            }
            Code.AGREE -> {
                holder.tag.text = "已通过"
            }
            Code.REJECT -> {
                holder.tag.text = "未通过"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = 0
        type = when (list[position].code) {
            MessageCode.FIF_ADD_YOU.id -> {
                Code.INVITATION
            }
            MessageCode.FOP_CONSENT.id -> {
                Code.ADDED
            }
            MessageCode.FIF_ADD_TO.id -> {
                Code.REQUEST
            }
            MessageCode.FIF_ADD_CONSENT.id -> {
                Code.AGREE
            }
            MessageCode.FIF_REJECT.id -> {
                Code.REJECT
            }
            else -> {
                Code.REFUSE
            }
        }
        return type
    }

    interface OnAgreeButtonClickedListener {
        fun onAgreeButtonClick(holder: NewFriendsViewHolder, position: Int)
    }

    fun setAgreeButtonClickedListener(listener: OnAgreeButtonClickedListener) {
        this.agreeListener = listener
    }

    interface OnRejectButtonClickedListener {
        fun onRejectButtonClick(holder: NewFriendsViewHolder, position: Int)
    }

    fun setRejectButtonClickedListener(listener: OnRejectButtonClickedListener) {
        this.rejectListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initNewFriendsList(list: MutableList<FriendInfoMessage>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateList(user: FriendInfoMessage) {
        for (i in 0 until list.size) {
            if (list[i].id == user.id) {
                list[i] = user
                notifyItemChanged(i)
                break
            }
        }
    }
}