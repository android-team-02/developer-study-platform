package com.sesac.developer_study_platform.ui

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.util.formatDate
import com.sesac.developer_study_platform.util.formatSystemMessage
import com.sesac.developer_study_platform.util.formatTime
import com.sesac.developer_study_platform.util.formatYearMonthDay
import com.sesac.developer_study_platform.util.getToday
import com.sesac.developer_study_platform.util.setImage

@BindingAdapter("dayTimeList")
fun setDayTimeList(view: TextView, dayList: List<String>?) {
    view.text = dayList?.joinToString("\n")
}

@BindingAdapter("dayList")
fun setDayList(view: TextView, dayList: List<String>?) {
    view.text = dayList?.joinToString(", ") { it.split(" ").first() }
}

@BindingAdapter("isEnabled")
fun setEnabled(view: AppCompatButton, study: Study?) {
    if (study != null) {
        view.isEnabled = !(formatYearMonthDay() > study.endDate
                || study.members.count() == study.totalMemberCount
                || study.banUsers.containsKey(Firebase.auth.uid))
    }
}

@BindingAdapter("sid", "image")
fun loadImage(view: ImageView, sid: String, image: String) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("${sid}/${image}")
    imageRef.downloadUrl.addOnSuccessListener {
        view.setImage(it.toString())
    }.addOnFailureListener {
        Log.e("loadImage", it.message ?: "error occurred.")
    }
}

@BindingAdapter("visible")
fun setVisibility(view: View, value: Boolean) {
    if (value) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("imageUrl")
fun loadImageUrl(view: ImageView, url: String?) {
    url?.let {
        view.setImage(it)
    }
}

@BindingAdapter("lastMessageTime")
fun setLastMessageTime(view: TextView, timestamp: String) {
    if (timestamp <= getToday()) {
        view.text = timestamp.formatTime()
    } else {
        view.text = timestamp.formatDate()
    }
}

@BindingAdapter("previousMessage", "message")
fun setSystemMessageVisibility(view: View, previousMessage: Message?, message: Message) {
    if (previousMessage != null) {
        if (previousMessage.timestamp.formatSystemMessage() < message.timestamp.formatSystemMessage()) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
        if (previousMessage.totalMemberCount != message.totalMemberCount) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } else {
        view.visibility = View.VISIBLE
    }
}

@BindingAdapter("previousMessageText", "messageText")
fun setSystemMessage(view: TextView, previousMessage: Message?, message: Message) {
    val messageTimestamp = message.timestamp.formatSystemMessage()
    if (previousMessage != null) {
        if (previousMessage.timestamp.formatSystemMessage() < messageTimestamp) {
            view.text = messageTimestamp
        }
        if (previousMessage.totalMemberCount < message.totalMemberCount) {
            view.text = view.context.getString(R.string.message_new_study_member)
        } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
            view.text = view.context.getString(R.string.message_left_study_member)
        }
    } else {
        view.text = messageTimestamp
    }
}