package com.playlab.superpomodoro.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask
import kotlin.time.Duration.Companion.minutes

class PomodoroViewModel: ViewModel() {
    private var timer: Timer? = null

    private var _pomodoroDuration = mutableStateOf(25.minutes.inWholeMilliseconds)
    val pomodoroDuration by _pomodoroDuration
    private var _shortBreakDuration = mutableStateOf(5.minutes.inWholeMilliseconds)
    val shortBreakDuration by _shortBreakDuration
    private var _longBreakDuration = mutableStateOf(15.minutes.inWholeMilliseconds)
    val longBreakDuration by _longBreakDuration

    private val _timeLeft = mutableStateOf(pomodoroDuration)
    val timeLeft by _timeLeft

    private val _pomodoroCount = mutableStateOf(0)
    val pomodoroCount by _pomodoroCount

    private val _timerStatus = MutableLiveData(TimerStatus.POMODORO)
    val timerStatus: MutableLiveData<TimerStatus>
        get() = _timerStatus

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
                        startBreakTimer(shortBreakDuration)
                        setTimeStatus(TimerStatus.SHORT_BREAK)

                    }else{
                        startBreakTimer(longBreakDuration)
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
                    startPomodoroTimer(pomodoroDuration)
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
        setTimeLeft(pomodoroDuration)
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
}