package com.sesac.developer_study_platform.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.data.Converters
import com.sesac.developer_study_platform.data.UserStudy

@Database(entities = [BookmarkStudy::class, UserStudy::class], version = 7)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun myStudyDao(): MyStudyDao
}