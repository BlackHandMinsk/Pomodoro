package com.example.pomodoro.stopwatch

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.START_TIME
import com.example.pomodoro.UNIT_TEN_MS
import com.example.pomodoro.databinding.StopwatchItemBinding
import com.example.pomodoro.displayTime
import com.example.stopwatch.MainActivity
import kotlinx.coroutines.*

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,
    private var period:Long = 0,
    private var circleTimerPeriod:Long = 0
    //private var timerStartTime:Long = 0


) : RecyclerView.ViewHolder(binding.root){
    private var timer: CountDownTimer? = null




    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        period = stopwatch.currentMs
    //    timerStartTime = stopwatch.currentMs
        binding.circleTimer.setPeriod(0)
        binding.circleTimer.setPeriod(stopwatch.timerStartTime)
        binding.circleTimer.setCurrent(0)
        binding.circleTimer.setCurrent(stopwatch.currentMs)




        if (stopwatch.isStarted) {
                startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }



    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs,stopwatch.timerStartTime)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.itemBackground.setBackgroundColor(Color.WHITE)
        binding.startPauseButton.text = "STOP"
        timer?.cancel()


//        CoroutineScope(Dispatchers.Main).launch {
//            timer = getCountDownTimer(stopwatch)
//            timer?.start()
//         }

        timer = getCountDownTimer(stopwatch)
        timer?.start()
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = "START"
        timer?.cancel()
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, UNIT_TEN_MS) {


            override fun onTick(millisUntilFinished: Long) {
                println(stopwatch.currentMs)
                stopwatch.currentMs = millisUntilFinished
               // binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                binding.circleTimer.setCurrent(millisUntilFinished)
                binding.stopwatchTimer.text = millisUntilFinished.displayTime()
            }

            override fun onFinish() {
                binding.circleTimer.setCurrent(0)
                stopwatch.currentMs = stopwatch.timerStartTime
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                binding.startPauseButton.text = "START"
                binding.itemBackground.setBackgroundColor(Color.RED)
                binding.blinkingIndicator.isInvisible = true
            }
        }
    }
}