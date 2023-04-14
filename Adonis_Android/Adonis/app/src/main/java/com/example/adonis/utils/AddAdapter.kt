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
import java.util.zip.Inflater

class AddAdapter: Adapter<AddAdapter.AddViewHolder>() {
    private var listener: OnAddButtonClickListener? = null
    private val searchResult = mutableListOf<FriendInfoMessage>()

    inner class AddViewHolder(itemView: View) : ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.add_avatar)
        val nickname: TextView = itemView.findViewById(R.id.add_nickname)
        val user: TextView = itemView.findViewById(R.id.add_id)
        val addButton: Button = itemView.findViewById(R.id.button_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_add, null)
        return AddViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return searchResult.size
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.addButton.setOnClickListener {
            if (listener != null) {
                listener?.onAddButtonClick(holder)
            }
        }
        holder.nickname.text = searchResult[position].nickname
        holder.user.text = searchResult[position].id
    }

    interface OnAddButtonClickListener {
        fun onAddButtonClick(holder: AddViewHolder)
    }

    fun setAddButtonClickListener(listener: OnAddButtonClickListener) {
        this.listener = listener
    }

    fun showResult(result: FriendInfoMessage) {
        searchResult.clear()
        searchResult.add(result)
    }

    fun showNotExistResult() {
        searchResult.clear()
    }
}