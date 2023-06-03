package com.example.adonis.utils

import android.annotation.SuppressLint
import com.example.adonis.entity.DialogueMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Dictionary
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class AdonisFunction {
    companion object {
        @JvmStatic
        fun longToDate(time: Long): Date {
            return Date(time)
        }

        @JvmStatic
        fun longToStringDate(time: Long): String {
            val type = if (abs(System.currentTimeMillis() - time) > 24 * 1000 * 60 * 60) {
                1
            } else {
                2
            }
            val format: String = if (type == 1) {
                "MM/dd/yy HH:mm"
            } else {
                "HH:mm"
            }
            val myDate = Date(time)
            return  dateToString(myDate, format)
        }

        @JvmStatic
        fun timeToLong(time: TimeResult): Long {
            return (time.hours*60*60 + time.minutes*60 + time.seconds)* 1000.toLong()
        }



        @JvmStatic
        fun longToTime(time: Long): TimeResult{
            val hours = time / (1000 * 60 * 60)
            val minutes = time % (1000 * 60 * 60) / (1000 * 60)
            val seconds = time % (1000 * 60 * 60) % (1000 * 60) / 1000
            return TimeResult(hours.toInt(), minutes.toInt(), seconds.toInt())
        }

        @SuppressLint("SimpleDateFormat")
        private fun dateToString(date: Date, format: String): String {
            return SimpleDateFormat(format).format(date)
        }

        @SuppressLint("SimpleDateFormat")
        private fun stringToDate(date: String, format: String): Date? {
            return SimpleDateFormat(format).parse(date)
        }
    }
}

class TimeResult(var hours: Int, var minutes: Int, var seconds: Int) {
    fun setTime(hours: Int, minutes: Int, seconds: Int) {
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }
}

class FindChatResult(val result: CopyOnWriteArrayList<DialogueMessage>, val position: CopyOnWriteArrayList<Int>)

