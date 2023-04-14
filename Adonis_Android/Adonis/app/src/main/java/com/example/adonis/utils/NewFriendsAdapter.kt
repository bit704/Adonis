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
        if (getItemViewType(position) == INVITATION) {
            holder.button.setOnClickListener {
                if (listener != null) {
                    listener?.onAgreeButtonClick(holder)
                }
            }
        } else {
            holder.button.isClickable = false
            holder.button.text = "待通过"
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = 0
        type = if (list[position].code == MessageCode.fif_add.id) {
            INVITATION
        } else {
            REQUEST
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
        this.list.addAll(list)
    }
}