package com.sesac.developer_study_platform.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sesac.developer_study_platform.data.BookmarkStudy

@Database(entities = [BookmarkStudy::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
}