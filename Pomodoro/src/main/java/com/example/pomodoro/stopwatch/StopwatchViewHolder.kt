package com.example.pomodoro.stopwatch

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.START_TIME
import com.example.pomodoro.UNIT_TEN_MS
import com.example.pomodoro.databinding.StopwatchItemBinding
import com.example.stopwatch.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,
    private var period:Long = 0


) : RecyclerView.ViewHolder(binding.root) {
    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        binding.circleTimer.setPeriod(stopwatch.currentMs)
      //  period = stopwatch.currentMs


        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }



    private fun initCircleTimer(stopwatch: Stopwatch){
        var current = stopwatch.currentMs
        GlobalScope.launch {
            while (current > period) {
                current -= UNIT_TEN_MS

                binding.circleTimer.setCurrent(current)
                delay(UNIT_TEN_MS)
            }
        }
    }



    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        initCircleTimer(stopwatch)
        binding.startPauseButton.text = "STOP"
        timer?.cancel()
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
            val interval = UNIT_TEN_MS


            override fun onTick(millisUntilFinished: Long) {
              //  stopwatch.currentMs -= interval
                println(stopwatch.currentMs)
               // binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                binding.stopwatchTimer.text = millisUntilFinished.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                binding.startPauseButton.text = "START"
                binding.itemBackground.setBackgroundColor(0xFF00FF00.toInt())
            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
      //  val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
               // ":${displaySlot(ms)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }
}