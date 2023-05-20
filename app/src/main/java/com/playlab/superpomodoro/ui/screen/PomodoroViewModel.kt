package com.playlab.superpomodoro.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
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
        const val DEFAULT_SOUND_PREFERENCE = true
        const val DEFAULT_VIBRATION_PREFERENCE = false
    }

    private var timer: Timer? = null

    val soundPreference = preferencesRepository.isSoundAllowed()
    val vibrationPreference = preferencesRepository.isVibrationAllowed()

    val pomodoroDurationPreference = preferencesRepository.pomodoroDuration()
    val shortBreakDurationPreference = preferencesRepository.shortBreakDuration()
    val longBreakDurationPreference = preferencesRepository.longBreakDuration()

    fun initPomodoroValues(
        pomodoroDuration: Long?,
        shortBreakDuration: Long?,
        longBreakDuration: Long?,
        isSoundAllowed: Boolean?,
        isVibrationAllowed: Boolean?
    ){
        pomodoroDuration?.let {
            setTimeLeft(it)
            _pomodoroDuration.value = it
        }
        shortBreakDuration?.let {
            _shortBreakDuration.value = it
        }
        longBreakDuration?.let {
            _longBreakDuration.value = it
        }
        isSoundAllowed?.let {
            _isSoundAllowed.value = it
        }
        isVibrationAllowed?.let {
            _isVibrationAllowed.value = it
        }
    }
    private val _isSoundAllowed = mutableStateOf(DEFAULT_SOUND_PREFERENCE)
    val isSoundAllowed = _isSoundAllowed

    private val _isVibrationAllowed = mutableStateOf(DEFAULT_VIBRATION_PREFERENCE)
    val isVibrationAllowed = _isVibrationAllowed

    private val _pomodoroDuration =
        mutableStateOf(DEFAULT_POMODORO_DURATION.minutes.inWholeMilliseconds)

    private val _shortBreakDuration =
        mutableStateOf(DEFAULT_SHORT_BREAK_DURATION.minutes.inWholeMilliseconds)

    private val _longBreakDuration =
        mutableStateOf(DEFAULT_LONG_BREAK_DURATION.minutes.inWholeMilliseconds)

    private val _timeLeft = mutableStateOf(_pomodoroDuration.value)
    val timeLeft by _timeLeft

    private val _pomodoroCount = mutableStateOf(0)
    val pomodoroCount by _pomodoroCount

    private val _timerStatus = MutableLiveData(TimerStatus.POMODORO)
    val timerStatus: LiveData<TimerStatus>
        get() = _timerStatus

    private var _isRunning  = mutableStateOf(false)
    val isRunning by _isRunning

    private val _endOfCycle = MutableLiveData<TimerStatus?>(null)
    val endOfCycle: LiveData<TimerStatus?>
        get() = _endOfCycle

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
        setTimeStatus(TimerStatus.POMODORO)
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                setTimeLeft(timeLeft - 1000)

                if (timeLeft <= 0) {
                    destroyTimer()
                    setPomodoroCount( pomodoroCount + 1 )
                    _endOfCycle.postValue(_timerStatus.value )
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
                    _endOfCycle.postValue(_timerStatus.value )
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
        setTimeStatus(TimerStatus.STOPPED)
        _endOfCycle.value = null
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