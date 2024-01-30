package com.sesac.developer_study_platform.util

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(resId: Int) {
    Snackbar.make(
        this,
        resId,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun ImageView.setImage(image: String) {
    Glide.with(this)
        .load(image)
        .centerCrop()
        .into(this)
}