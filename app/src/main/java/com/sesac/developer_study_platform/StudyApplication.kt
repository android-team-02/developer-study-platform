package com.sesac.developer_study_platform

import android.app.Application
import android.content.SharedPreferences

class StudyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        sharedPref = applicationContext.getSharedPreferences(
            getString(R.string.pref_file_key),
            MODE_PRIVATE
        )
    }

    companion object {
        lateinit var sharedPref: SharedPreferences
    }
}