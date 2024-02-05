package com.sesac.developer_study_platform

import android.app.Application
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
        db = Room.databaseBuilder(this, AppDatabase::class.java, "study-database")
            .fallbackToDestructiveMigration()
            .build()
        bookmarkDao = db.bookmarkDao()
        githubRepository = GithubRepository()
        studyRepository = StudyRepository()
        bookmarkRepository = BookmarkRepository()
    }

    override fun onTerminate() {
        db.close()
        super.onTerminate()
    }

    companion object {
        lateinit var bookmarkDao: BookmarkDao
        lateinit var githubRepository: GithubRepository
        lateinit var studyRepository: StudyRepository
        lateinit var bookmarkRepository: BookmarkRepository
    }
}