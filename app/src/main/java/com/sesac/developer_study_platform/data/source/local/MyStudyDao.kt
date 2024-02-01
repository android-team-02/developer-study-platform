package com.sesac.developer_study_platform.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sesac.developer_study_platform.data.UserStudy

@Dao
interface MyStudyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStudy(userStudy: UserStudy)

    @Query("DELETE FROM my_studies")
    suspend fun deleteAllMyStudies()

    @Query("SELECT * FROM my_studies")
    suspend fun getAllMyStudies(): List<UserStudy>
}