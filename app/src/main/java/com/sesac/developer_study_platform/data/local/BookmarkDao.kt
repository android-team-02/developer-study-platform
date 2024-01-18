package com.sesac.developer_study_platform.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sesac.developer_study_platform.data.BookmarkStudy

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insertBookmarkStudy(study: BookmarkStudy)

    @Query("DELETE FROM bookmark_studies WHERE sid = :sid")
    suspend fun deleteBookmarkStudyBySid(sid: String)

    @Query("SELECT * FROM bookmark_studies WHERE sid = :sid")
    suspend fun getBookmarkStudyBySid(sid: String): List<BookmarkStudy>

    @Query("SELECT * FROM bookmark_studies ORDER BY id DESC")
    suspend fun getAllBookmarkStudy(): List<BookmarkStudy>
}