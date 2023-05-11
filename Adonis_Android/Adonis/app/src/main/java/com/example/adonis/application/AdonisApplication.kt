package com.example.adonis.application

import android.app.Application
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.MessageCode

class AdonisApplication: Application() {
    private var myID: String? = null
    private val newFriendsData = mutableListOf<FriendInfoMessage>()
    private val contactsData = mutableListOf<FriendInfoMessage>()
    private val newsListData = mutableMapOf<String, MutableList<DialogueMessage>>()
    private val latestNewsData = mutableMapOf<String, DialogueMessage>()
    private val orderData = mutableListOf<String>()

    override fun onCreate() {
        super.onCreate()
    }

    fun getMyID(): String? {return myID}
    fun getContacts(): MutableList<FriendInfoMessage>{return contactsData}
    fun getNewsList(): MutableMap<String, MutableList<DialogueMessage>>{return newsListData}
    fun getOrderData(): MutableList<String> {return orderData}
    fun getNewFriends(): MutableList<FriendInfoMessage>{return newFriendsData}
    fun getLatestNewsData(): MutableMap<String, DialogueMessage> {return latestNewsData}
    fun initInfo(contacts: MutableList<FriendInfoMessage>, newsList:MutableList<DialogueMessage>) {
        for (item in contacts) {
            if (item.code == MessageCode.fif_already_add.id) {
                contactsData.add(item)
            }
            else {
                newFriendsData.add(item)
            }
        }
        initNewsList(newsList)
    }
    private fun initNewsList(newsList: MutableList<DialogueMessage>) {
        var id: String? = null
        for (item in newsList) {
            id = if (item.senderId != myID) {
                item.senderId
            } else {
                item.receiverId
            }
            if (newsListData.contains(id)) {
                newsListData[id]?.add(item)
            } else {
                val list = mutableListOf<DialogueMessage>()
                list.add(item)
                newsListData[id] = list
                orderData.add(0, id)
            }
            latestNewsData[id] = item
        }
    }
    fun initMyID(id: String) {myID = id}
    fun addContacts(info: FriendInfoMessage) {contactsData.add(info)}
    fun addNewFriends(info: FriendInfoMessage) {newFriendsData.add(info)}
    fun addNewsList(info: DialogueMessage) {
        var id: String? = null
        id = if (info.senderId != myID) {
            info.senderId
        } else {
            info.receiverId
        }
        if (newsListData.contains(id)) {
            newsListData[id]?.add(info)
        } else {
            val list = mutableListOf<DialogueMessage>()
            list.add(info)
            newsListData[id] = list
            orderData.add(id)
        }
        latestNewsData[id] = info
    }

    fun findUserByID(id: String?): FriendInfoMessage? {
        for (user in contactsData) {
            if (user.id == id){
                return user
            }
        }
        return null
    }

    fun findChatByID(id: String?): MutableList<DialogueMessage>? {
        return if (newsListData.containsKey(id)) {
            newsListData[id]
        } else {
            mutableListOf()
        }
    }
}