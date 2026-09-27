package com.mkstudio.FitnessMusicCounter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkstudio.FitnessMusicCounter.model.TabataPhase
import com.mkstudio.FitnessMusicCounter.model.WorkoutConfig
import com.mkstudio.FitnessMusicCounter.model.WorkoutMode
import com.mkstudio.FitnessMusicCounter.repo.MainRepository
import com.mkstudio.FitnessMusicCounter.repo.db.WorkoutRecord
import com.mkstudio.FitnessMusicCounter.util.CommonUtil
import com.mkstudio.FitnessMusicCounter.util.Constants
import com.mkstudio.FitnessMusicCounter.util.SoundUtil
import com.mkstudio.FitnessMusicCounter.util.myLogD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetLap(
    val setNumber: Int,
    val durationText: String,
    val seconds: Long
)

@HiltViewModel
class CountViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {

    private var totalTimerJob: Job? = null
    private var modeTimerJob: Job? = null
    private var startTimestamp: Long = 0L

    private val timeTracks = mutableListOf<Long>()

    private val _config = MutableStateFlow(WorkoutConfig())
    val config: StateFlow<WorkoutConfig> = _config.asStateFlow()

    private val _workoutTime = MutableStateFlow(Constants.STR_DEFUALT_WORKOUT_TIME)
    val workoutTime: StateFlow<String> = _workoutTime.asStateFlow()

    private val _repsCnt = MutableStateFlow(0)
    val repsCnt: StateFlow<Int> = _repsCnt.asStateFlow()

    private val _laps = MutableStateFlow<List<SetLap>>(emptyList())
    val laps: StateFlow<List<SetLap>> = _laps.asStateFlow()

    // Mode 1: Tabata
    private val _tabataPhase = MutableStateFlow(TabataPhase.PREPARE)
    val tabataPhase: StateFlow<TabataPhase> = _tabataPhase.asStateFlow()

    private val _phaseSecondsRemaining = MutableStateFlow(3)
    val phaseSecondsRemaining: StateFlow<Int> = _phaseSecondsRemaining.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    // Mode 2: Auto Rest Timer
    private val _isResting = MutableStateFlow(false)
    val isResting: StateFlow<Boolean> = _isResting.asStateFlow()

    private val _restSecondsRemaining = MutableStateFlow(0)
    val restSecondsRemaining: StateFlow<Int> = _restSecondsRemaining.asStateFlow()

    private val _isCompleted = MutableSharedFlow<Boolean>()
    val isCompleted: SharedFlow<Boolean> = _isCompleted.asSharedFlow()

    fun initWorkout(workoutConfig: WorkoutConfig) {
        stopAllTimers()
        _config.value = workoutConfig
        _repsCnt.value = 0
        _laps.value = emptyList()
        _isResting.value = false
        _isPaused.value = false
        timeTracks.clear()
        timeTracks.add(System.currentTimeMillis())

        startTotalTimer()

        when (workoutConfig.mode) {
            WorkoutMode.TABATA -> startTabataLoop()
            WorkoutMode.REST_TIMER -> {
                _isResting.value = false
                _restSecondsRemaining.value = workoutConfig.restDurationSeconds
            }
            WorkoutMode.MANUAL -> {
                // Manual counting
            }
        }
    }

    private fun startTotalTimer() {
        totalTimerJob?.cancel()
        startTimestamp = System.currentTimeMillis()
        totalTimerJob = viewModelScope.launch {
            while (isActive) {
                val elapsedSec = (System.currentTimeMillis() - startTimestamp) / 1000
                val sec = elapsedSec % 60
                val min = (elapsedSec / 60) % 60
                val hour = elapsedSec / 3600
                _workoutTime.value = String.format("%02d:%02d:%02d", hour, min, sec)
                delay(1000)
            }
        }
    }

    // --- Mode 1: TABATA Implementation ---
    private fun startTabataLoop() {
        modeTimerJob?.cancel()
        modeTimerJob = viewModelScope.launch {
            // Prepare Phase (3 seconds)
            _tabataPhase.value = TabataPhase.PREPARE
            for (sec in 3 downTo 1) {
                _phaseSecondsRemaining.value = sec
                SoundUtil.playShortBeep()
                delay(1000)
            }
            SoundUtil.playHighBeep()

            val targetSets = _config.value.targetSets
            val workDuration = _config.value.workDurationSeconds
            val restDuration = _config.value.restDurationSeconds

            for (setIndex in 1..targetSets) {
                _repsCnt.value = setIndex

                // WORK Phase
                _tabataPhase.value = TabataPhase.WORK
                for (sec in workDuration downTo 1) {
                    _phaseSecondsRemaining.value = sec
                    if (sec <= 3) SoundUtil.playShortBeep()
                    delay(1000)
                }
                SoundUtil.playHighBeep()

                recordLap(setIndex, "${workDuration}s Work")

                if (setIndex == targetSets) break

                // REST Phase
                _tabataPhase.value = TabataPhase.REST
                for (sec in restDuration downTo 1) {
                    _phaseSecondsRemaining.value = sec
                    if (sec <= 3) SoundUtil.playShortBeep()
                    delay(1000)
                }
                SoundUtil.playHighBeep()
            }

            // Finish
            _tabataPhase.value = TabataPhase.FINISHED
            finishWorkout()
        }
    }

    fun toggleTabataPause() {
        _isPaused.value = !_isPaused.value
        // Simple pause/resume could be expanded or handled
    }

    // --- Mode 2: Auto Rest Timer Implementation ---
    fun completeSetAndStartRest() {
        val target = _config.value.targetSets
        if (_repsCnt.value >= target) return

        val nextSetNum = _repsCnt.value + 1
        _repsCnt.value = nextSetNum

        val now = System.currentTimeMillis()
        val prev = if (timeTracks.isNotEmpty()) timeTracks.last() else now
        val diffSec = ((now - prev) / 1000).coerceAtLeast(1)
        timeTracks.add(now)

        val nMins = diffSec / 60
        val nSecs = diffSec % 60
        recordLap(nextSetNum, String.format("%02d:%02d", nMins, nSecs))

        if (nextSetNum >= target) {
            finishWorkout()
            return
        }

        // Start Rest Countdown
        startRestCountdown(_config.value.restDurationSeconds)
    }

    private fun startRestCountdown(seconds: Int) {
        modeTimerJob?.cancel()
        _isResting.value = true
        _restSecondsRemaining.value = seconds

        modeTimerJob = viewModelScope.launch {
            for (sec in seconds downTo 1) {
                _restSecondsRemaining.value = sec
                if (sec <= 3) {
                    SoundUtil.playShortBeep()
                }
                delay(1000)
            }
            SoundUtil.playHighBeep()
            _isResting.value = false
        }
    }

    fun skipRest() {
        modeTimerJob?.cancel()
        _isResting.value = false
        SoundUtil.playHighBeep()
    }

    fun addRestTime(secondsToAdd: Int = 15) {
        val current = _restSecondsRemaining.value
        startRestCountdown(current + secondsToAdd)
    }

    // --- Mode 3: Manual Counter Implementation ---
    fun addManualSet() {
        val target = _config.value.targetSets
        if (_repsCnt.value >= target) return

        val nextSetNum = _repsCnt.value + 1
        _repsCnt.value = nextSetNum

        val now = System.currentTimeMillis()
        val prev = if (timeTracks.isNotEmpty()) timeTracks.last() else now
        val diffSec = ((now - prev) / 1000).coerceAtLeast(1)
        timeTracks.add(now)

        val nMins = diffSec / 60
        val nSecs = diffSec % 60
        recordLap(nextSetNum, String.format("%02d:%02d", nMins, nSecs))

        if (_repsCnt.value >= target) {
            finishWorkout()
        }
    }

    fun removeManualSet() {
        if (_repsCnt.value > 0) {
            _repsCnt.value = _repsCnt.value - 1
            if (timeTracks.size > 1) {
                timeTracks.removeAt(timeTracks.lastIndex)
            }
            if (_laps.value.isNotEmpty()) {
                _laps.value = _laps.value.dropLast(1)
            }
        }
    }

    private fun recordLap(setNum: Int, durationText: String) {
        val newLap = SetLap(setNum, durationText, 0)
        _laps.value = _laps.value + newLap
    }

    private fun finishWorkout() {
        stopAllTimers()
        saveToDatabase()
        SoundUtil.playFinishBeep()
        viewModelScope.launch {
            _isCompleted.emit(true)
        }
    }

    fun stopAllTimers() {
        totalTimerJob?.cancel()
        totalTimerJob = null
        modeTimerJob?.cancel()
        modeTimerJob = null
    }

    fun saveToDatabase() {
        if (_laps.value.isEmpty()) return

        val strDatetime = CommonUtil.getTodayWithTimeFormatted()
        val modeTag = "[${_config.value.mode.title}]"
        val strTrackLog = buildString {
            append("$modeTag ")
            _laps.value.forEach { lap ->
                append("Set ${lap.setNumber} - ${lap.durationText};")
            }
        }

        val record = WorkoutRecord(0, strDatetime, strTrackLog)
        viewModelScope.launch {
            repo.insertSuspend(record)
            myLogD("Saved workout record: $record")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllTimers()
    }
}