package com.example.adonis.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.FriendInfoMessage

class ContactsAdapter: RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {
    private var contacts = mutableListOf<FriendInfoMessage>()
    private var listener: OnItemClickedListener? = null

    inner class ContactsHolder(itemView: View) : ViewHolder(itemView){
        val avatar = itemView.findViewById<ImageView>(R.id.contacts_avatar)
        val nickname = itemView.findViewById<TextView>(R.id.contacts_nickname)
        var id: String? = null
        var remark: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_contacts, null)
        return ContactsHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.id = contacts[position].id
        if (contacts[position].customNickname != null) {
            holder.nickname.text = contacts[position].customNickname
            holder.remark = contacts[position].customNickname
        } else {
            holder.nickname.text = contacts[position].nickname
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClick(holder)
        }

    }

    fun initContacts(contacts: MutableList<FriendInfoMessage>) {
        this.contacts = contacts
    }

    fun addContacts(user: FriendInfoMessage) {
        contacts.add(user)
    }

    interface OnItemClickedListener {
        fun onItemClick(holder: ContactsHolder)
    }

    fun setItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }
}