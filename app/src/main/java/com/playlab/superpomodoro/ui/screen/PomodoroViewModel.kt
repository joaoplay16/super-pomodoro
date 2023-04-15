package com.playlab.superpomodoro.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlab.superpomodoro.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    companion object {
        const val DEFAULT_POMODORO_DURATION = 25
        const val DEFAULT_SHORT_BREAK_DURATION = 5
        const val DEFAULT_LONG_BREAK_DURATION = 15
    }

    private var timer: Timer? = null

    val isSoundAllowed = preferencesRepository.isSoundAllowed()
    val isVibrationAllowed = preferencesRepository.isVibrationAllowed()

    val pomodoroDuration = preferencesRepository.pomodoroDuration()
    private val _pomodoroDuration =
        mutableStateOf(DEFAULT_POMODORO_DURATION.minutes.inWholeMilliseconds)

    val shortBreakDuration = preferencesRepository.shortBreakDuration()
    private val _shortBreakDuration =
        mutableStateOf(DEFAULT_SHORT_BREAK_DURATION.minutes.inWholeMilliseconds)

    val longBreakDuration = preferencesRepository.longBreakDuration()
    private val _longBreakDuration =
        mutableStateOf(DEFAULT_LONG_BREAK_DURATION.minutes.inWholeMilliseconds)

    private val _timeLeft = mutableStateOf(_pomodoroDuration.value)
    val timeLeft by _timeLeft

    private val _pomodoroCount = mutableStateOf(0)
    val pomodoroCount by _pomodoroCount

    private val _timerStatus = MutableLiveData(TimerStatus.POMODORO)
    val timerStatus: MutableLiveData<TimerStatus>
        get() = _timerStatus

    private var _isRunning  = mutableStateOf(false)
    val isRunning by _isRunning

    init {
        viewModelScope.launch {
            pomodoroDuration.collect{
                it?.let {
                    setTimeLeft(it)
                    _pomodoroDuration.value = it
                }
            }
            shortBreakDuration.collect{
                it?.let {
                    _shortBreakDuration.value = it
                }
            }
            longBreakDuration.collect{
                it?.let {
                    _longBreakDuration.value = it
                }
            }
        }
    }

    fun startTimer() {
        if(timer == null) { // Avoid creation of multiple instances of Timer
            if (timerStatus.value in arrayOf(TimerStatus.LONG_BREAK, TimerStatus.SHORT_BREAK)) {
                startBreakTimer(timeLeft)
            } else {
                startPomodoroTimer(timeLeft)
            }
        }
    }

    private fun startPomodoroTimer(duration: Long) {
        setTimeLeft(duration)
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                setTimeLeft(timeLeft - 1000)

                if (timeLeft <= 0) {
                    destroyTimer()
                    setPomodoroCount( pomodoroCount + 1 )

                    if(pomodoroCount < 4){
                        startBreakTimer(_shortBreakDuration.value)
                        setTimeStatus(TimerStatus.SHORT_BREAK)

                    }else{
                        startBreakTimer(_longBreakDuration.value)
                        setTimeStatus(TimerStatus.LONG_BREAK)
                    }
                }
            }
        }, 0, 1000)
    }

    private fun startBreakTimer(duration: Long) {
        setTimeLeft(duration)
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                setTimeLeft(timeLeft - 1000)
                if (timeLeft <= 0) {
                    if(pomodoroCount == 4) setPomodoroCount(0)
                    setTimeStatus(TimerStatus.POMODORO)
                    destroyTimer()
                    startPomodoroTimer(_pomodoroDuration.value)
                }
            }
        }, 0, 1000)
    }


    fun pauseTimer(){
        destroyTimer()
    }

    fun stopTimer(){
        destroyTimer()

        setPomodoroCount(0)
        setTimeLeft(_pomodoroDuration.value)
        setTimeStatus(TimerStatus.POMODORO)
    }

    private fun destroyTimer() {
        timer?.cancel()
        timer = null
    }

    fun setTimeLeft(timeLeft: Long){
        _timeLeft.value = timeLeft
    }

    fun setPomodoroCount(count: Int){
        _pomodoroCount.value = count
    }

    fun setTimeStatus(timeStatus: TimerStatus){
        _timerStatus.postValue( timeStatus)
    }

    fun setRunning(running: Boolean){
        _isRunning.value = running
    }

    fun setPomodoroDuration(duration: Long){
        viewModelScope.launch {
            preferencesRepository.setPomodoroDuration(duration)
        }
    }

    fun setShortBreakDuration(duration: Long){
        viewModelScope.launch {
            preferencesRepository.setShortBreakDuration(duration)
        }
    }

    fun setLongBreakDuration(duration: Long){
        viewModelScope.launch {
            preferencesRepository.setLongBreakDuration(duration)
        }
    }

    fun allowSound(allowed: Boolean){
        viewModelScope.launch {
            preferencesRepository.allowSound(allowed)
        }
    }

    fun allowVibration(allowed: Boolean){
        viewModelScope.launch {
            preferencesRepository.allowVibration(allowed)
        }
    }
}