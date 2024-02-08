package com.sesac.developer_study_platform

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.sesac.developer_study_platform.data.source.local.AppDatabase
import com.sesac.developer_study_platform.data.source.local.BookmarkDao
import com.sesac.developer_study_platform.data.source.local.BookmarkRepository
import com.sesac.developer_study_platform.data.source.remote.GithubRepository
import com.sesac.developer_study_platform.data.source.remote.StudyRepository

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
        studyRepository = StudyRepository()
        bookmarkRepository = BookmarkRepository()
        githubRepository = GithubRepository()
    }

    override fun onTerminate() {
        db.close()
        super.onTerminate()
    }

    companion object {
        lateinit var sharedPref: SharedPreferences
        lateinit var bookmarkDao: BookmarkDao
        lateinit var studyRepository: StudyRepository
        lateinit var bookmarkRepository: BookmarkRepository
        lateinit var githubRepository: GithubRepository
    }
}