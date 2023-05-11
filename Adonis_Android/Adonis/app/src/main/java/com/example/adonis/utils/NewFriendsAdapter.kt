package com.example.adonis.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.MessageCode

class NewFriendsAdapter: Adapter<NewFriendsAdapter.NewFriendsViewHolder>() {
    private var list = mutableListOf<FriendInfoMessage>()
    private var listener: OnAgreeButtonClickedListener? = null

    private val REQUEST: Int = 1
    private val INVITATION: Int = 2
    private val ADDED: Int = 3
    private val AGREE: Int = 4
    private val REFUSE: Int = 5

    inner class NewFriendsViewHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.new_friends_avatar)
        val nickname: TextView = itemView.findViewById(R.id.new_friends_nickname)
        val request: TextView = itemView.findViewById(R.id.new_friends_request)
        val button: Button = itemView.findViewById(R.id.button_new_friends)
        var id: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_new_friends, null)
        return NewFriendsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewFriendsViewHolder, position: Int) {

        holder.nickname.text = list[position].nickname
        holder.request.text = list[position].memo
        holder.id = list[position].id

        holder.button.isClickable = false
        when(getItemViewType(position))  {
            INVITATION-> {
                holder.button.isClickable = true
                holder.button.setOnClickListener {
                   if (listener != null) {
                       listener?.onAgreeButtonClick(holder)
                   }
                }
            }
            REQUEST -> {
                holder.button.text = "待通过"
            }
            ADDED -> {
                holder.button.text = "已通过"
            }
            REFUSE -> {
                holder.button.text = "已拒绝"
            }
            AGREE -> {
                holder.button.text = "已添加"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = 0
        type = when (list[position].code) {
            MessageCode.FIF_ADD_YOU.id -> {
                INVITATION
            }
            MessageCode.FIF_OP_SUCCESS.id -> {
                ADDED
            }
            MessageCode.FIF_ADD_TO.id -> {
                REQUEST
            }
            MessageCode.FIF_ADD_CONSENT.id -> {
                AGREE
            }
            else -> {
                REFUSE
            }
        }
        return type
    }

    interface OnAgreeButtonClickedListener {
        fun onAgreeButtonClick(holder: NewFriendsViewHolder)
    }

    fun setAgreeButtonClickedListener(listener: OnAgreeButtonClickedListener) {
        this.listener = listener
    }

    fun initNewFriendsList(list: MutableList<FriendInfoMessage>) {
        this.list = list
    }

    fun addNewFriends(user: FriendInfoMessage) {
        list.add(0, user)
        notifyItemInserted(0)
        notifyItemChanged(0)
    }
    fun updateStates(position: Int) {
        list[position].code = MessageCode.FIF_OP_SUCCESS.id
        notifyItemChanged(position)
    }
}