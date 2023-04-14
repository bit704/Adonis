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

    inner class ContactsHolder(itemView: View) : ViewHolder(itemView){
        val avatar = itemView.findViewById<ImageView>(R.id.contacts_avatar)
        val nickname = itemView.findViewById<TextView>(R.id.contacts_nickname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_contacts, null)
        return ContactsHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.nickname.text = contacts[position].nickname
    }

    fun initContacts(contacts: List<FriendInfoMessage>) {
        this.contacts.addAll(contacts.toMutableList())
    }

    fun addContacts(user: FriendInfoMessage) {
        contacts.add(user)
    }
}