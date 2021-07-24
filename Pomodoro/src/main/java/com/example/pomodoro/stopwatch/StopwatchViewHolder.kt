package com.example.pomodoro.stopwatch

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.*
import com.example.pomodoro.databinding.StopwatchItemBinding

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,



) : RecyclerView.ViewHolder(binding.root){


    private var timer: CountDownTimer? = null




    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = (stopwatch.currentMsStart-stopwatch.currentMs).displayTime()
        binding.circleTimer.setPeriod(stopwatch.currentMsStart)
        binding.circleTimer.setCurrent(stopwatch.currentMsStart-stopwatch.currentMs)


        if (stopwatch.isLaunched && stopwatch.currentMs == 0L){
            binding.itemBackground.setBackgroundColor(Color.RED)
            binding.deleteButton.setBackgroundColor(Color.RED)
        } else {
            binding.itemBackground.setBackgroundColor(Color.TRANSPARENT)
            binding.deleteButton.setBackgroundColor(Color.TRANSPARENT)
        }


        if (stopwatch.isStarted) {
            binding.startPauseButton.text = "STOP"
            startTimer(stopwatch)
        }
        else {
            binding.startPauseButton.text = "START"
            stopTimer()
        }
        initButtonsListeners(stopwatch)
    }



    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs,stopwatch.timerStartTime)
            } else {
                if (!stopwatch.isLaunched){
                    stopwatch.timerStartTime = System.currentTimeMillis()
                } else stopwatch.timerStartTime = System.currentTimeMillis()-stopwatch.currentMs
                listener.start(stopwatch.id,stopwatch.timerStartTime)
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.circleTimer.setCurrent(0L)
            timer?.cancel()
            listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.itemBackground.setBackgroundColor(Color.TRANSPARENT)
        binding.deleteButton.setBackgroundColor(Color.TRANSPARENT)
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {

            override fun onTick(millisUntilFinished: Long) {

                stopwatch.currentMs = (System.currentTimeMillis()-stopwatch.timerStartTime)

                binding.circleTimer.setPeriod(stopwatch.currentMsStart)
                binding.circleTimer.setCurrent(stopwatch.currentMsStart-stopwatch.currentMs)

                binding.stopwatchTimer.text = (stopwatch.currentMsStart-stopwatch.currentMs).displayTime()



                if (stopwatch.currentMs >= stopwatch.currentMsStart) {
                    stopwatch.currentMs = 0L
                    stopTimer()
                    listener.stop(stopwatch.id, stopwatch.currentMs, stopwatch.timerStartTime)
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }
        }
    }
}