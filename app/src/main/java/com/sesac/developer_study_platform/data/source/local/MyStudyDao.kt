package com.sesac.developer_study_platform.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.flow.Flow

@Dao
interface MyStudyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStudy(userStudy: UserStudy)

    @Query("SELECT * FROM my_studies ORDER BY `sid` DESC")
    fun getAllMyStudies(): LiveData<List<UserStudy>>

    @Query("SELECT * FROM my_studies ORDER BY `sid` DESC")
    fun getAllMyStudiesFlow(): Flow<List<UserStudy>>

    @Query("DELETE FROM my_studies")
    suspend fun deleteAllMyStudies()
}