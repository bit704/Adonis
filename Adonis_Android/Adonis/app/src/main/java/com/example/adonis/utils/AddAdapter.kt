package com.example.adonis.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.UserInfoMessage
import java.util.zip.Inflater

class AddAdapter: Adapter<AddAdapter.AddViewHolder>() {
    private var listener: OnAddButtonClickListener? = null
    private var itemListener: OnItemClickListener? = null
    private val searchResult = mutableListOf<FriendInfoMessage>()
    private var myID: String? = null
    private var tag: Boolean = false

    inner class AddViewHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.add_avatar)
        val nickname: TextView = itemView.findViewById(R.id.add_nickname)
        val user: TextView = itemView.findViewById(R.id.add_id)
        val addButton: Button = itemView.findViewById(R.id.button_add)
        val item: LinearLayout = itemView.findViewById(R.id.add_item)
        var data: FriendInfoMessage? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_add, null)
        return AddViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return searchResult.size
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.item.setOnClickListener {
            if (itemListener != null) {
                itemListener?.onItemClick(holder)
            }
        }

        if (tag) {
            holder.addButton.visibility = ViewGroup.GONE
        } else {
            holder.addButton.visibility = ViewGroup.VISIBLE
            holder.addButton.setOnClickListener {
                if (listener != null) {
                    listener?.onAddButtonClick(holder)
                }
            }
        }
        holder.nickname.text = searchResult[position].nickname
        holder.user.text = searchResult[position].id
        holder.data = searchResult[position]
    }

    fun initMyID(id: String) {
        myID = id
    }

    interface OnAddButtonClickListener {
        fun onAddButtonClick(holder: AddViewHolder)
    }

    interface OnItemClickListener {
        fun onItemClick(holder: AddViewHolder)
    }

    fun setAddButtonClickListener(listener: OnAddButtonClickListener) {
        this.listener = listener
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        this.itemListener = listener
    }

    fun showResult(result: FriendInfoMessage, tag: Boolean) {
        if (searchResult.size > 0) {
            searchResult.clear()
            notifyItemRemoved(0)
        }
        searchResult.add(result)
        this.tag = tag
        notifyItemInserted(0)
    }

    fun showNotExistResult() {
        if (searchResult.size > 0) {
            searchResult.clear()
            notifyItemRemoved(0)
        }
    }
}