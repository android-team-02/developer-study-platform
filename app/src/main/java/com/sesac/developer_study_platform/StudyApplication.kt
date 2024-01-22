package com.sesac.developer_study_platform

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.sesac.developer_study_platform.data.local.AppDatabase
import com.sesac.developer_study_platform.data.local.BookmarkDao

class StudyApplication : Application() {

    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        sharedPref = applicationContext.getSharedPreferences(
            getString(R.string.application_pref_file_key),
            MODE_PRIVATE
        )
        db = Room.databaseBuilder(this, AppDatabase::class.java, "study-database")
            .fallbackToDestructiveMigration()
            .build()
        bookmarkDao = db.bookmarkDao()
    }

    override fun onTerminate() {
        db.close()
        super.onTerminate()
    }

    companion object {
        lateinit var sharedPref: SharedPreferences
        lateinit var bookmarkDao: BookmarkDao
    }
}