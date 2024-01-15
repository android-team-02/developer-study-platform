package com.sesac.developer_study_platform

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(resId: Int) {
    Snackbar.make(
        this,
        resId,
        Snackbar.LENGTH_SHORT
    ).show()
}