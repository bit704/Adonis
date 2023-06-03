package com.example.adonis.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.adonis.entity.DialogueMessage
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage

class DataBaseHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        var sql: String?
        sql =
            "create table ${FilterString.NEW_FRIENDS_TABLE}(\n" +
                    "code int NOT NULL,\n" +
                    "id varchar(20),\n" +
                    "nickname varchar(20),\n" +
                    "customNickname varchar(20),\n" +
                    "memo varchar(20)\n" +
                    ");"
        p0?.execSQL(sql)
        sql =
            "create table ${FilterString.CONTACTS_TABLE}(\n" +
                    "code int NOT NULL,\n" +
                    "id varchar(20),\n" +
                    "nickname varchar(20),\n" +
                    "customNickname varchar(20),\n" +
                    "memo varchar(20)\n" +
                    ");"
        p0?.execSQL(sql)

        sql =
            "create table ${FilterString.NEWS_TABLE}(\n" +
                    "senderId varchar(20),\n" +
                    "receiverId varchar(20),\n" +
                    "content varchar(500),\n" +
                    "lastedTime bigint,\n" +
                    "occurredTime bigint NOT NULL\n" +
                    ");"
        p0?.execSQL(sql)

        sql =
            "create table ${FilterString.LATEST_NEWS_TABLE}(\n" +
                    "senderId varchar(20),\n" +
                    "receiverId varchar(20),\n" +
                    "content varchar(500),\n" +
                    "lastedTime bigint,\n" +
                    "occurredTime bigint NOT NULL\n" +
                    ");"
        p0?.execSQL(sql)

        sql =
            "create table ${FilterString.LASTED_NEWS_TABLE}(\n" +
                    "senderId varchar(20),\n" +
                    "receiverId varchar(20),\n" +
                    "content varchar(500),\n" +
                    "lastedTime bigint,\n" +
                    "occurredTime bigint NOT NULL\n" +
                    ");"
        p0?.execSQL(sql)

        sql =
            "create table ${FilterString.UNREAD_NEWS_TABLE}(\n" +
                    "senderId varchar(20),\n" +
                    "receiverId varchar(20),\n" +
                    "content varchar(500),\n" +
                    "lastedTime bigint,\n" +
                    "occurredTime bigint NOT NULL\n" +
                    ");"
        p0?.execSQL(sql)

        sql =
            "create table ${FilterString.LAST_TIME_TABLE}(\n" +
                    "id varchar(20),\n" +
                    "lastedTime bigint NOT NULL\n" +
                    ");"
        p0?.execSQL(sql)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


    companion object {
        @SuppressLint("SdCardPath")
        @JvmStatic
        fun checkDBExist(dbNAME: String): Boolean {
            var checkDB: SQLiteDatabase? = null
            val dbPATH = "/data/data/com.example.adonis/databases/"
            try {
                val path = dbPATH + dbNAME
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            checkDB?.close()
            return checkDB != null
        }

        @SuppressLint("Recycle", "Range")
        @JvmStatic
        fun selectTimeData(database: SQLiteDatabase, table: String): MutableMap<String, Long> {
            val map = mutableMapOf<String, Long>()
            val sql = buildString {
                append("select * from ")
                append(table)
            }
            val cursor: Cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                for (i in 0 until  cursor.count) {
                    val id = cursor.getString(cursor.getColumnIndex(FilterString.ID))
                    val time = cursor.getLong(cursor.getColumnIndex(FilterString.LASTED_TIME))

                    map[id] = time
                    cursor.moveToNext()
                }
            }

            return map
        }

        @SuppressLint("Recycle", "Range")
        @JvmStatic
        fun selectFriendData(database: SQLiteDatabase, table: String, myID: String?): MutableList<FriendInfoMessage> {
            val list = mutableListOf<FriendInfoMessage>()
            val sql = buildString {
                append("select * from ")
                append(table)
            }
            val cursor: Cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                for (i in 0 until  cursor.count) {
                    val user = FriendInfoMessage()
                    user.code = cursor.getInt(cursor.getColumnIndex(FilterString.CODE))
                    user.id = cursor.getString(cursor.getColumnIndex(FilterString.ID))
                    user.nickname = cursor.getString(cursor.getColumnIndex(FilterString.NICKNAME))
                    user.customNickname = cursor.getString(cursor.getColumnIndex(FilterString.CUSTOM_NICKNAME))
                    user.memo = cursor.getString(cursor.getColumnIndex(FilterString.MEMO))


                    list.add(user)
                    cursor.moveToNext()
                }
            }

            return list
        }

        @SuppressLint("Recycle", "Range")
        @JvmStatic
        fun selectNewsData(database: SQLiteDatabase, table: String,
                           userID: String? = null, myID: String? = null,
                           sortType: Int = 2, selectAll: Boolean = false):
                MutableList<DialogueMessage> {
            val list = mutableListOf<DialogueMessage>()
            val sql = if ((table == FilterString.NEWS_TABLE || table == FilterString.LASTED_NEWS_TABLE) && !selectAll) {
                if (userID == myID) {
                    buildString {
                        append("select * from $table ")
                        append("where ${FilterString.SENDER_ID}='$userID' and ")
                        append("${FilterString.RECEIVER_ID}='$userID'")
                    }
                }
                else {
                    buildString {
                        append("select * from $table ")
                        append("where ${FilterString.SENDER_ID}='$userID' or ")
                        append("${FilterString.RECEIVER_ID}='$userID'")
                    }
                }
            } else {
                buildString {
                    append("select * from $table")
                }
            }
            val cursor: Cursor = database.rawQuery(sql, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                for (i in 0 until  cursor.count) {
                    val msg = DialogueMessage()
                    msg.senderId = cursor.getString(cursor.getColumnIndex(FilterString.SENDER_ID))
                    msg.receiverId = cursor.getString(cursor.getColumnIndex(FilterString.RECEIVER_ID))
                    msg.content = cursor.getString(cursor.getColumnIndex(FilterString.CONTENT))
                    msg.occurredTime = cursor.getLong(cursor.getColumnIndex(FilterString.OCCURRED_TIME))
                    msg.lastedTime = cursor.getLong(cursor.getColumnIndex(FilterString.LASTED_TIME))

                    list.add(msg)
                    cursor.moveToNext()
                }
            }

            if (sortType == 1) {
                list.sortWith { msg1, msg2 ->
                    msg2.occurredTime.compareTo(msg1.occurredTime)
                }
            } else {
                list.sortWith { msg1, msg2 ->
                    msg1.occurredTime.compareTo(msg2.occurredTime)
                }
            }

            return list
        }

        @JvmStatic
        fun <T>insertData(database: SQLiteDatabase, table: String, data: T?) {
            val content = ContentValues()
            when (data) {
                is FriendInfoMessage -> {
                    content.put(FilterString.CODE, data.code)
                    content.put(FilterString.ID, data.id)
                    content.put(FilterString.NICKNAME, data.nickname)
                    content.put(FilterString.CUSTOM_NICKNAME, data.customNickname)
                    content.put(FilterString.MEMO, data.memo)
                }
                is DialogueMessage -> {
                    content.put(FilterString.SENDER_ID, data.senderId)
                    content.put(FilterString.RECEIVER_ID, data.receiverId)
                    content.put(FilterString.CONTENT, data.content)
                    content.put(FilterString.OCCURRED_TIME, data.occurredTime)
                    content.put(FilterString.LASTED_TIME, data.lastedTime)
                }
            }
            database.insert(table, null, content)
        }

        @JvmStatic
        fun insertTimeData(database: SQLiteDatabase, time: Long, id: String) {
            val content = ContentValues()
            content.put(FilterString.ID, id)
            content.put(FilterString.LASTED_TIME, time)
            database.insert(FilterString.LAST_TIME_TABLE, null, content)
        }

        @JvmStatic
        fun updateTimeData(database: SQLiteDatabase, time: Long, id: String) {
            val content = ContentValues()
            content.put(FilterString.LASTED_TIME, time)
            database.update(
                FilterString.LAST_TIME_TABLE, content, "${FilterString.ID}=?",
                listOf(id).toTypedArray()
            )
        }

        @SuppressLint("Recycle")
        @JvmStatic
        fun <T>updateData(database: SQLiteDatabase, table: String, othersID: String? = null, data: T?) {
            val content = ContentValues()
            when (data) {
                is FriendInfoMessage -> {
                    content.put(FilterString.CODE, data.code)
                    content.put(FilterString.NICKNAME, data.nickname)
                    content.put(FilterString.CUSTOM_NICKNAME, data.customNickname)
                    content.put(FilterString.MEMO, data.memo)

                    if (!othersID.isNullOrEmpty()) {
                        val sql = buildString {
                            append("select * from $table ")
                            append("where ${FilterString.ID}='$othersID'")
                        }
                        val cursor: Cursor = database.rawQuery(sql, null)
                        if (cursor.count == 0) {
                            val temp = ContentValues()
                            temp.put(FilterString.CODE, data.code)
                            temp.put(FilterString.ID, data.id)
                            temp.put(FilterString.NICKNAME, data.nickname)
                            temp.put(FilterString.CUSTOM_NICKNAME, data.customNickname)
                            temp.put(FilterString.MEMO, data.memo)
                            database.insert(table, null, temp)
                        } else {
                            database.update(
                                table, content, "${FilterString.ID}=?",
                                listOf(data.id).toTypedArray()
                            )
                        }
                    } else {
                        database.update(table, content, "${FilterString.ID}=?",
                            listOf(data.id).toTypedArray())
                    }
                }
                is DialogueMessage -> {
                    if (othersID.isNullOrEmpty()) {
                        return
                    }
                    content.put(FilterString.SENDER_ID, data.senderId)
                    content.put(FilterString.RECEIVER_ID, data.receiverId)
                    content.put(FilterString.CONTENT, data.content)
                    content.put(FilterString.OCCURRED_TIME, data.occurredTime)
                    content.put(FilterString.LASTED_TIME, data.lastedTime)
                    if (data.senderId == data.receiverId) {
                        database.update(
                            table, content,
                            "${FilterString.SENDER_ID} = ? and ${FilterString.RECEIVER_ID} = ?",
                            listOf(othersID, othersID).toTypedArray()
                        )
                    } else {
                        database.update(
                            table, content,
                            "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                            listOf(othersID, othersID).toTypedArray()
                        )
                    }
                }
            }
        }

        @JvmStatic
        fun <T>deleteData(database: SQLiteDatabase, table: String, othersID: String? = null, data: T?) {
            when (data) {
                is FriendInfoMessage -> {
                    database.delete(
                        table, "${FilterString.ID}=?",
                        listOf(data.id).toTypedArray()
                    )

                    if (othersID.isNullOrEmpty()) {
                        return
                    }

                    database.delete(FilterString.NEWS_TABLE,
                        "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                        listOf(othersID, othersID).toTypedArray()
                    )
                    database.delete(FilterString.UNREAD_NEWS_TABLE,
                        "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                        listOf(othersID, othersID).toTypedArray()
                    )
                    database.delete(FilterString.LASTED_NEWS_TABLE,
                        "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                        listOf(othersID, othersID).toTypedArray()
                    )
                    database.delete(FilterString.LATEST_NEWS_TABLE,
                        "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                        listOf(othersID, othersID).toTypedArray()
                    )

                }

                is DialogueMessage -> {
                    if (othersID.isNullOrEmpty()) {
                        return
                    }
                    if (data.senderId == data.receiverId) {
                        database.delete(table,
                            "${FilterString.SENDER_ID} = ? and ${FilterString.RECEIVER_ID} = ?",
                            listOf(othersID, othersID).toTypedArray()
                        )
                    } else {
                        database.delete(table,
                            "${FilterString.SENDER_ID} = ? or ${FilterString.RECEIVER_ID} = ?",
                            listOf(othersID, othersID).toTypedArray()
                        )
                    }
                }
            }
        }

        @SuppressLint("Recycle")
        @JvmStatic
        fun queryData(database: SQLiteDatabase, table: String, userID: String?, myID: String?): Boolean {
            var sql: String? = null
            when (table) {
                FilterString.CONTACTS_TABLE ->
                    sql = buildString {
                        append("select * from $table ")
                        append("where ${FilterString.ID}='$userID'")
                    }
                FilterString.LATEST_NEWS_TABLE ->
                    sql = if (userID == myID) {
                        buildString {
                            append("select * from $table ")
                            append("where ${FilterString.SENDER_ID}='$userID' and ")
                            append("${FilterString.RECEIVER_ID}='$userID'")
                        }
                    } else {
                        buildString {
                            append("select * from $table ")
                            append("where ${FilterString.SENDER_ID}='$userID' or ")
                            append("${FilterString.RECEIVER_ID}='$userID'")
                        }
                    }
            }
            val cursor: Cursor = database.rawQuery(sql, null)
            return cursor.count > 0
        }

        @SuppressLint("Recycle")
        @JvmStatic
        fun updateLastedTime(database: SQLiteDatabase): Boolean {
            val updateLasted = buildString {
                append("update ${FilterString.LASTED_NEWS_TABLE} ")
                append("set ${FilterString.LASTED_TIME}=${FilterString.LASTED_TIME}-1000 ")
                append("where ${FilterString.LASTED_TIME} >= 1000")
            }
            database.execSQL(updateLasted)

            val deleteLasted = buildString {
                append("delete from ${FilterString.LASTED_NEWS_TABLE} ")
                append("where ${FilterString.LASTED_TIME}<=0")
            }
            database.execSQL(deleteLasted)

            val selectSql = buildString {
                append("select * from ${FilterString.LASTED_NEWS_TABLE}")
            }

            val cursor: Cursor = database.rawQuery(selectSql, null)
            return cursor.count>0
        }

        @SuppressLint("Recycle")
        @JvmStatic
        fun updateUnread(database: SQLiteDatabase, userID: String?, myID: String?) {
            val insertSql = if (userID == myID) {
                buildString {
                    append("insert into ${FilterString.LASTED_NEWS_TABLE}  ")
                    append("select * from ${FilterString.UNREAD_NEWS_TABLE} ")
                    append("where ${FilterString.LASTED_TIME}>0 and ")
                    append("(${FilterString.SENDER_ID}='$userID' and ")
                    append("${FilterString.RECEIVER_ID}='$userID')")
                }
            } else {
                buildString {
                    append("insert into ${FilterString.LASTED_NEWS_TABLE}  ")
                    append("select * from ${FilterString.UNREAD_NEWS_TABLE} ")
                    append("where ${FilterString.LASTED_TIME}>0 and ")
                    append("(${FilterString.SENDER_ID}='$userID' or ")
                    append("${FilterString.RECEIVER_ID}='$userID')")
                }
            }
            try {
                database.execSQL(insertSql)
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }

            val deleteSql = if (userID == myID) {
                buildString {
                    append("delete from ${FilterString.UNREAD_NEWS_TABLE} ")
                    append("where ${FilterString.SENDER_ID}='$userID' and ")
                    append("${FilterString.RECEIVER_ID}='$userID'")
                }
            } else {
                buildString {
                    append("delete from ${FilterString.UNREAD_NEWS_TABLE} ")
                    append("where ${FilterString.SENDER_ID}='$userID' or ")
                    append("${FilterString.RECEIVER_ID}='$userID'")
                }
            }
            try {
                database.execSQL(deleteSql)
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }


        }
    }
}