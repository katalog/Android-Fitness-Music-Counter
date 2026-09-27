package com.mkstudio.FitnessMusicCounter.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutRecordDao {
    @Query("SELECT * from tb_workoutrecord ORDER BY id DESC")
    fun getAllFlow(): Flow<List<WorkoutRecord>>

    @Query("SELECT * from tb_workoutrecord ORDER BY id DESC")
    fun getAll(): List<WorkoutRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuspend(record: WorkoutRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: WorkoutRecord)

    @Delete
    suspend fun deleteSuspend(record: WorkoutRecord)

    @Delete
    fun delete(record: WorkoutRecord)
}