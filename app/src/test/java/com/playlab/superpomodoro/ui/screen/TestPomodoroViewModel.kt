package com.playlab.superpomodoro.ui.screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.playlab.superpomodoro.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.Timer
import java.util.TimerTask
import kotlin.time.ExperimentalTime

class TestPomodoroViewModel {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PomodoroViewModel
    private var timer: Timer? = null

    @Before
    fun setUp() {
        timer = Timer()
//        viewModel = PomodoroViewModel()
    }

    @After
    fun tearDown() {
//        viewModel.stopTimer()
        timer = null
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    @Test
    fun shouldResetPomodoroCountAfterFourthCycle() = runTest{
        with(viewModel){
//            setPomodoroCount(0)
            startTimer()
            val pomodoroDuration = 25 * 60 * 1000L
            val shortBreakDuration = 5 * 60 * 1000L
            val longBreakDuration = 15 * 60 * 1000L


//            println("TIME LEFT ${timeLeft.value / (60 * 1000L)}")

            Thread.sleep(2000)
//            assertThat(pomodoroCount.value).isEqualTo(0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTimerPassage() = runTest{

        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                println("TICK")
            }
        }, 0, 1000)


        withContext(Dispatchers.IO) {
            Thread.sleep(6000)
        }
    }



//    @Test
//    fun `test timer countdown`() {
//        // Define um observer para a propriedade timerStatus
//        val observer = Observer<PomodoroViewModel.TimerStatus> {}
//        viewModel.timerStatus.observeForever(observer)
//
//        // Inicia um pomodoro de 25 minutos
//        viewModel.startTimer()
//
//        // Simula a passagem do tempo por 1 minuto
//        advanceTimeBy(1 * 60 * 1000)
//
//        // Verifica se o timerStatus corresponde a um pomodoro em andamento
//        assertEquals(PomodoroViewModel.TimerStatus.POMODORO, viewModel.timerStatus.value)
//
//        // Simula a passagem do tempo por 25 minutos
//        advanceTimeBy(25 * 60 * 1000)
//
//        // Verifica se o timerStatus corresponde a um intervalo curto
//        assertEquals(PomodoroViewModel.TimerStatus.SHORT_BREAK, viewModel.timerStatus.value)
//
//        // Simula a passagem do tempo por 5 minutos
//        advanceTimeBy(5 * 60 * 1000)
//
//        // Verifica se o timerStatus corresponde a um pomodoro em andamento
//        assertEquals(PomodoroViewModel.TimerStatus.POMODORO, viewModel.timerStatus.value)
//
//        // Finaliza o observer
//        viewModel.timerStatus.removeObserver(observer)
//    }
//
//    private fun advanceTimeBy(timeInMillis: Long) {
//        val currentTime = 0
//        viewModel.remainingTime.value = (currentTime + timeInMillis)
//    }
}
