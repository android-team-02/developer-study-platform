package com.sesac.developer_study_platform

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun Int.showSnackbar(view: View) {
    Snackbar.make(
        view,
        this,
        Snackbar.LENGTH_SHORT
    ).show()
}