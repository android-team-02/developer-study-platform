package com.sesac.developer_study_platform.ui

import android.util.Log
import android.widget.ImageView
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
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

@BindingAdapter("image")
fun loadImage(view: ImageView, study: UserStudy) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("${study.sid}/${study.image}")
    imageRef.downloadUrl.addOnSuccessListener {
        view.setImage(it.toString())
    }.addOnFailureListener {
        Log.e("loadImage", it.message ?: "error occurred.")
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view)
            .load(url)
            .centerCrop()
            .into(view)
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