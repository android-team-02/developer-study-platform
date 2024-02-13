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
import com.sesac.developer_study_platform.util.convertTimestampToDate
import com.sesac.developer_study_platform.util.convertTimestampToTime
import com.sesac.developer_study_platform.util.formatYearMonthDay
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
fun setLastMessageTime(view: TextView, timestamp: Long) {
    if (timestamp.convertTimestampToDate() < System.currentTimeMillis().convertTimestampToDate()) {
        view.text = timestamp.convertTimestampToDate()
    } else {
        view.text = timestamp.convertTimestampToTime()
    }
}

@BindingAdapter("dateFlowPrevMessage", "dateFlowMessage")
fun setDateFlowVisibility(view: View, previousMessage: Message?, message: Message) {
    if (previousMessage != null) {
        if (previousMessage.timestamp.convertTimestampToDate() < message.timestamp.convertTimestampToDate()) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } else {
        view.visibility = View.VISIBLE
    }
}

@BindingAdapter("studyMemberFlowPrevMessage", "studyMemberFlowMessage")
fun setStudyMemberFlowVisibility(view: View, previousMessage: Message?, message: Message) {
    if (previousMessage != null) {
        if (previousMessage.totalMemberCount != message.totalMemberCount) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("datePrevMessage", "dateMessage")
fun setDateMessage(view: TextView, previousMessage: Message?, message: Message) {
    val messageTimestamp = message.timestamp.convertTimestampToDate()
    if (previousMessage != null) {
        if (previousMessage.timestamp.convertTimestampToDate() < messageTimestamp) {
            view.text = messageTimestamp
        }
    } else {
        view.text = messageTimestamp
    }
}

@BindingAdapter("studyMemberPrevMessage", "studyMemberMessage")
fun setStudyMemberMessage(view: TextView, previousMessage: Message?, message: Message) {
    if (previousMessage != null) {
        if (previousMessage.totalMemberCount < message.totalMemberCount) {
            view.text = view.context.getString(R.string.message_new_study_member)
        } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
            view.text = view.context.getString(R.string.message_left_study_member)
        }
    }
}