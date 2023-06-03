package com.example.adonis.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adonis.R
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage

class ContactsAdapter: RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {
    private var contacts = mutableListOf<FriendInfoMessage>()
    private var listener: OnItemClickedListener? = null
    private var myID: String? = null

    inner class ContactsHolder(itemView: View) : ViewHolder(itemView){
        val avatar: ImageView = itemView.findViewById(R.id.contacts_avatar)
        val nickname: TextView = itemView.findViewById(R.id.contacts_nickname)
        var data: FriendInfoMessage? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_contacts, null)
        return ContactsHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.data = contacts[position]
        if (!contacts[position].customNickname.isNullOrBlank()) {
            holder.nickname.text = contacts[position].customNickname
        } else {
            holder.nickname.text = contacts[position].nickname
        }
        if (holder.data!!.id == myID)
            holder.nickname.text = FilterString.ME
        holder.itemView.setOnClickListener {
            listener?.onItemClick(holder)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initContacts(contacts: MutableList<FriendInfoMessage>, id: String?) {
        this.contacts = contacts
        myID = id
        notifyDataSetChanged()
    }

    fun addContacts(user: FriendInfoMessage) {
        contacts.add(user)
        notifyItemInserted(itemCount - 1)
        notifyItemChanged(itemCount - 1)
    }

    fun deleteContacts(id: String) {
        var tag: Int = -1
        for (i in 0 until contacts.size) {
            if (contacts[i].id == id) {
                tag = i
                break
            }
        }
        if (tag != -1) {
            contacts.removeAt(tag)
            notifyItemRemoved(tag)
        }
    }

    fun updateContacts(info: FriendInfoMessage) {
        for (i in 0 until contacts.size) {
            if (contacts[i].id == info.id) {
                contacts[i].customNickname = info.customNickname
                notifyItemChanged(i)
                break
            }
        }
    }

    interface OnItemClickedListener {
        fun onItemClick(holder: ContactsHolder)
    }

    fun setItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }
}