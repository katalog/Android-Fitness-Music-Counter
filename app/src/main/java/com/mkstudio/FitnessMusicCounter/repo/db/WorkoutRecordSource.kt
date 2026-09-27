package com.mkstudio.FitnessMusicCounter.repo.db

import com.mkstudio.FitnessMusicCounter.util.myLogD
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRecordSource @Inject constructor(private val workoutRecordDao: WorkoutRecordDao) {
    fun getAllFlow(): Flow<List<WorkoutRecord>> {
        return workoutRecordDao.getAllFlow()
    }

    fun getAll(): List<WorkoutRecord> {
        myLogD("db src get all")
        return workoutRecordDao.getAll()
    }

    suspend fun insertSuspend(record: WorkoutRecord) {
        myLogD("db src in suspend = $record")
        workoutRecordDao.insertSuspend(record)
    }

    fun insert(record: WorkoutRecord) {
        myLogD("db src in = $record")
        workoutRecordDao.insert(record)
    }

    suspend fun deleteSuspend(record: WorkoutRecord) {
        workoutRecordDao.deleteSuspend(record)
    }

    fun delete(record: WorkoutRecord) {
        workoutRecordDao.delete(record)
    }
}