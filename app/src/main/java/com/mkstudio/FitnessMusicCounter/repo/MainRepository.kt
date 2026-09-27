package com.mkstudio.FitnessMusicCounter.repo

import com.mkstudio.FitnessMusicCounter.repo.db.WorkoutRecord
import com.mkstudio.FitnessMusicCounter.repo.db.WorkoutRecordSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val db: WorkoutRecordSource,
    private val prep: LocalPreferences
) {
    fun getAllFlow(): Flow<List<WorkoutRecord>> = db.getAllFlow()

    fun getAll(): List<WorkoutRecord> = db.getAll()

    suspend fun insertSuspend(record: WorkoutRecord) = db.insertSuspend(record)

    fun insert(record: WorkoutRecord) = db.insert(record)

    suspend fun deleteSuspend(record: WorkoutRecord) = db.deleteSuspend(record)

    fun delete(record: WorkoutRecord) = db.delete(record)

    // local preferences
    fun getReps(): Int = prep.getReps()

    fun keepReps(v: Int) = prep.keepReps(v)

    fun getIsFirstRun(): Boolean = prep.getIsFirstRun()

    fun keepIsFirstRun(v: Boolean) = prep.keepIsFirstRun(v)

    fun isShowAdmobToday(): Boolean = prep.isShowAdmobToday()

    fun keepShowAdmobToday() = prep.keepShowAdmobToday()
}