package com.example.adonis.application

import android.app.Activity
import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import com.example.adonis.entity.Code
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.MessageCode
import com.example.adonis.utils.DataBaseHelper
import com.example.adonis.utils.FindChatResult
import java.util.concurrent.CopyOnWriteArrayList


class AdonisApplication: Application() {
    private var myInfo: FriendInfoMessage? = null
    private var myID: String? = null
    private val newFriendsData = mutableListOf<FriendInfoMessage>()
    private val contactsData = mutableListOf<FriendInfoMessage>()
    private val newsListData = mutableMapOf<String, MutableList<DialogueMessage>>()
    private val unReadNewsListData = mutableMapOf<String, MutableList<DialogueMessage>>()
    private val latestNewsData = mutableMapOf<String, CopyOnWriteArrayList<DialogueMessage>>()
    private val orderData = mutableListOf<String>()
    private var isForeground = true
    private val timeData = mutableMapOf<String, Long>()

    private var myDB: SQLiteDatabase? = null

    fun getMyID(): String? {return myID}
    fun getMyInfo(): FriendInfoMessage? {return myInfo}
    fun getLastTime(): MutableMap<String, Long> {return timeData}
    fun getIsForeground(): Boolean {return  isForeground}
    fun getContacts(): MutableList<FriendInfoMessage>{return contactsData}
//    fun getNewsList(): MutableMap<String, MutableList<DialogueMessage>>{return newsListData}
    fun getUnReadNewsListData(): MutableMap<String, MutableList<DialogueMessage>> {return unReadNewsListData}
    fun getOrderData(): MutableList<String> {return orderData}
    fun getNewFriends(): MutableList<FriendInfoMessage>{return newFriendsData}
    fun getLatestNewsData(): MutableMap<String, CopyOnWriteArrayList<DialogueMessage>> {return latestNewsData}
    fun initFromDB() {
        newFriendsData.addAll(DataBaseHelper.selectFriendData(myDB!!, FilterString.NEW_FRIENDS_TABLE, myID))
        contactsData.addAll(DataBaseHelper.selectFriendData(myDB!!, FilterString.CONTACTS_TABLE, myID))
        timeData.putAll(DataBaseHelper.selectTimeData(myDB!!, FilterString.LAST_TIME_TABLE))

        initMyInfo(findUserByID(myID)!!)
        val list = DataBaseHelper.selectNewsData(myDB!!, FilterString.LATEST_NEWS_TABLE, sortType = 2)
        val lastedList = DataBaseHelper.selectNewsData(myDB!!, FilterString.LASTED_NEWS_TABLE, sortType = 2, selectAll = true)
        val initLatestList = mutableListOf<DialogueMessage>()
        initLatestList.addAll(list)
        initLatestList.addAll(lastedList)
        initLatestList.sortWith { msg1, msg2 -> msg1.occurredTime.compareTo(msg2.occurredTime) }
        for (msg in initLatestList) {
            val othersID = if (msg.senderId != myID) {
                msg.senderId
            } else {
                msg.receiverId
            }

            if (latestNewsData.contains(othersID)) {
                if (msg.lastedTime > 0) {
                    latestNewsData[othersID]?.add(msg)
                } else {
                    latestNewsData[othersID]?.clear()
                    latestNewsData[othersID]?.add(msg)
                }
                orderData.remove(othersID)
                orderData.add(0, othersID)
            } else {
                val temp = CopyOnWriteArrayList<DialogueMessage>()
                temp.add(msg)
                latestNewsData[othersID] = temp
                orderData.add(0, othersID)
                unReadNewsListData[othersID] = mutableListOf()
            }
        }

        val unReadList = DataBaseHelper.selectNewsData(myDB!!, FilterString.UNREAD_NEWS_TABLE)
        for (msg in unReadList) {
            val othersID = if (msg.senderId != myID) {
                msg.senderId
            } else {
                msg.receiverId
            }
            if (unReadNewsListData.containsKey(othersID)) {
                unReadNewsListData[othersID]!!.add(msg)
            } else {
                val temp = mutableListOf<DialogueMessage>()
                temp.add(msg)
                unReadNewsListData[othersID] = temp
            }
        }

    }
    fun initInfo(contacts: MutableList<FriendInfoMessage>, newsList:MutableList<DialogueMessage>?) {
        val contactsList = mutableListOf<FriendInfoMessage>()
        val newFriendList = mutableListOf<FriendInfoMessage>()
        var myInfo = FriendInfoMessage()
        for (item in contacts) {
            if (item.code == MessageCode.FIF_TWO_WAY.id || item.code == MessageCode.FIF_SINGLE_FOR_YOU.id) {
                if (item.id == myID)
                    myInfo = item
                else
                    contactsList.add(item)
            } else if (item.code == MessageCode.FIF_SINGLE_ON_YOU.id) {
                continue
            }
            else {
                newFriendList.add(item)
            }
        }
        contactsList.add(0, myInfo)
        val contactsDiffer = getMapDifferent(contactsData, contactsList)
        val newFriendDiffer = getMapDifferent(newFriendsData, newFriendList)
        contactsData.clear()
        contactsData.addAll(contactsList)

        for ((key, value ) in contactsDiffer) {
            when (value) {
                Code.EXIST -> continue
                Code.UPDATE ->
                    DataBaseHelper.updateData(myDB!!, FilterString.CONTACTS_TABLE, data = key)
                Code.DELETE ->
                    DataBaseHelper.deleteData(myDB!!, FilterString.CONTACTS_TABLE, data = key)
                Code.ADD ->
                    DataBaseHelper.insertData(myDB!!, FilterString.CONTACTS_TABLE,  key)
            }
        }
        for ((key, value ) in newFriendDiffer) {
            Log.i("Add", key.toString()+value)
            when (value) {
                Code.EXIST -> continue
                Code.DELETE -> {
                    if (findUserByID(key.id) != null) {
                        if (key.code == MessageCode.FIF_ADD_TO.id) {
                            key.code = MessageCode.FIF_ADD_CONSENT.id
                            DataBaseHelper.updateData(
                                myDB!!,
                                FilterString.NEW_FRIENDS_TABLE,
                                data = key
                            )
                        }
                    }
                }
                Code.UPDATE -> {
                    DataBaseHelper.updateData(myDB!!, FilterString.NEW_FRIENDS_TABLE, data = key)
                    for (i in 0 until newFriendsData.size) {
                        if (newFriendsData[i].id == key.id) {
                            if (key.code == MessageCode.FIF_ADD_YOU.id) {
                                newFriendsData.removeAt(i)
                                newFriendsData.add(0, key)
                            } else {
                                newFriendsData[i] = key
                            }
                            continue
                        }
                    }
                }
                Code.ADD -> {
                    DataBaseHelper.insertData(myDB!!, FilterString.NEW_FRIENDS_TABLE, key)
                    newFriendsData.add(0, key)
                }
            }
        }
        if (newsList != null)
            initNewsList(newsList)
    }
    private fun initNewsList(newsList: MutableList<DialogueMessage>) {
        var id: String

        for (item in newsList) {
            id = if (item.senderId != myID) {
                item.senderId
            } else {
                item.receiverId
            }
            if(item.lastedTime <= 0)
                DataBaseHelper.insertData(myDB!!, FilterString.NEWS_TABLE, item)
            DataBaseHelper.insertData(myDB!!, FilterString.UNREAD_NEWS_TABLE, item)

            if (latestNewsData.contains(id)) {
                if (item.lastedTime <= 0) {
                    DataBaseHelper.updateData(myDB!!, FilterString.LATEST_NEWS_TABLE, id, item)
                }
                orderData.remove(id)
                orderData.add(0, id)
            } else {
                if (item.lastedTime <= 0) {
                    DataBaseHelper.insertData(myDB!!, FilterString.LATEST_NEWS_TABLE, item)
                }
                latestNewsData[id] = CopyOnWriteArrayList()
                orderData.add(0, id)
            }

            if (!unReadNewsListData.containsKey(id)) {
                val temp  = mutableListOf<DialogueMessage>()
                temp.add(item)
                unReadNewsListData[id] = temp
            } else {
                unReadNewsListData[id]!!.add(item)
            }
        }
    }
    fun initMyID(id: String) {
        myID = id
    }

    private fun initMyInfo(info: FriendInfoMessage) {
        myInfo = info
    }
    fun initMyDB(db: SQLiteDatabase?) {myDB = db}

    fun addNews(news: DialogueMessage, id: String?) {
        newsListData[id]!!.add(news)
    }

    fun deleteNews(id: String?) {
        newsListData.remove(id)
        unReadNewsListData.remove(id)
    }

    fun findUserByID(id: String?): FriendInfoMessage? {
        for (user in contactsData) {
            if (user.id == id){
                return user
            }
        }
        return null
    }


    fun findChatByID(id: String?): FindChatResult {
        val result = CopyOnWriteArrayList<DialogueMessage>()
        val position = CopyOnWriteArrayList<Int>()
        val lastedNews = DataBaseHelper.selectNewsData(myDB!!, FilterString.LASTED_NEWS_TABLE, id, myID, 2)
        result.addAll(lastedNews)
        if (newsListData.containsKey(id)) {
            result.addAll(newsListData[id]!!)
        } else {
            val dataList = DataBaseHelper.selectNewsData(myDB!!, FilterString.NEWS_TABLE, id, myID, 2)
            newsListData[id.toString()] = dataList
            result.addAll(dataList)
        }

        result.sortWith { msg1, msg2 ->
            msg1.occurredTime.compareTo(msg2.occurredTime)
        }
        for (pos in 0 until result.size) {
            if (result[pos].lastedTime > 0) {
                position.add(pos)
            }
        }

        return FindChatResult(result, position)
    }

    fun updateNewFriendsList(user: FriendInfoMessage) {
        val id = user.id
        for (pos in 0 until newFriendsData.size) {
            if (newFriendsData[pos].id == id) {
                if (user.code == MessageCode.FIF_ADD_YOU.id) {
                    newFriendsData.removeAt(pos)
                    newFriendsData.add(0, user)
                    return
                } else {
                    newFriendsData[pos] = user
                }
            }
        }
        newFriendsData.add(0, user)
    }

    fun getNewFriendsNum(): Int {
        var num = 0
        for (user in newFriendsData) {
            if (user.code == MessageCode.FIF_ADD_YOU.id) {
                num += 1
            }
        }
        return num
    }

    fun exit() {
        myID = null
        newFriendsData.clear()
        contactsData.clear()
        newsListData.clear()
        latestNewsData.clear()
        orderData.clear()
        unReadNewsListData.clear()
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks{
            private var liveActivity = 0

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {

            }

            override fun onActivityStarted(p0: Activity) {
                if (liveActivity == 0) {
                    isForeground = true
                    Log.i("BackToForeground", "BackToForeground")
                }
                liveActivity++
            }

            override fun onActivityResumed(p0: Activity) {

            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStopped(p0: Activity) {
                liveActivity--
                if (liveActivity == 0) {
                    isForeground = false
                    Log.i("BackRun", "BackRun")
                }
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(p0: Activity) {

            }
        })
    }

    private fun getMapDifferent(
        oldList: MutableList<FriendInfoMessage>,
        newList: MutableList<FriendInfoMessage>)
    : MutableMap<FriendInfoMessage, Int> {
        val map = mutableMapOf<String, FriendInfoMessage>()
        val countMap = mutableMapOf<String, Int>()
        val resultMap = mutableMapOf<FriendInfoMessage, Int>()
        for (user in oldList) {
            map[user.id] = user
            countMap[user.id] = Code.DELETE
        }
        for (user in newList) {
            if (map.containsKey(user.id)) {
                if (map[user.id]!! == user) {
                    countMap[user.id] = Code.EXIST
                } else {
                    countMap[user.id] = Code.UPDATE
                    map[user.id] = user
                }
            } else {
                countMap[user.id] = Code.ADD
                map[user.id] = user
            }
        }
        for ((key, value) in countMap) {
            val user: FriendInfoMessage? = map[key]
            resultMap[user!!] = value
        }
        return resultMap
    }

}