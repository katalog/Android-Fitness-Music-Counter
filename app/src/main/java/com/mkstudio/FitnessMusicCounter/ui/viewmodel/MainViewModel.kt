package com.mkstudio.FitnessMusicCounter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkstudio.FitnessMusicCounter.radio.RadioServiceManager
import com.mkstudio.FitnessMusicCounter.repo.MainRepository
import com.mkstudio.FitnessMusicCounter.repo.db.WorkoutRecord
import com.mkstudio.FitnessMusicCounter.util.Constants
import com.mkstudio.FitnessMusicCounter.util.myLogD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repo: MainRepository,
    val radio: RadioServiceManager
) : ViewModel() {

    val records: StateFlow<List<WorkoutRecord>> = repo.getAllFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _repsMax = MutableStateFlow(10)
    val repsMax: StateFlow<Int> = _repsMax.asStateFlow()

    val stationName: StateFlow<String> = radio.currentStation
    val isPlaying: StateFlow<Boolean> = radio.isPlaying

    init {
        myLogD("main view model init...")
        loadRepsMax()
    }

    fun loadRepsMax() {
        val saved = repo.getReps()
        _repsMax.value = if (saved > 0) saved else 10
    }

    fun setRepsMax(v: Int) {
        _repsMax.value = v
        repo.keepReps(v)
    }

    fun playRadio(station: String) {
        radio.playRadio(station)
    }

    fun toggleRadioPlay() {
        radio.togglePlay()
    }

    fun stopRadio() {
        radio.stopRadio()
    }

    fun bindRadio() {
        radio.bindRadio()
    }

    fun unbindRadio() {
        radio.unbindRadio()
    }

    fun deleteRecord(record: WorkoutRecord) {
        viewModelScope.launch {
            repo.deleteSuspend(record)
        }
    }
}