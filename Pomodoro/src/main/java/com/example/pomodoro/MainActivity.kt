package com.example.stopwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foregroundservice.ForegroundService
import com.example.pomodoro.COMMAND_ID
import com.example.pomodoro.COMMAND_START
import com.example.pomodoro.COMMAND_STOP
import com.example.pomodoro.STARTED_TIMER_TIME_MS
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.stopwatch.Stopwatch
import com.example.pomodoro.stopwatch.StopwatchAdapter
import com.example.pomodoro.stopwatch.StopwatchListener

class MainActivity : AppCompatActivity(),LifecycleObserver, StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0
    private var startTime:Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }



        binding.addNewStopwatchButton.setOnClickListener {
            startTime = binding.inputTime.text.toString().toLong()
            println("starttimechange"+startTime)
            stopwatches.add(Stopwatch(nextId++, startTime.toLong()*60000, startTime.toLong()*60000,false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }


    }

    override fun start(id: Int) {
        changeStopwatch(id, null, null,true)
    }

    override fun stop(id: Int, currentMs: Long, timerStartTime: Long) {
        changeStopwatch(id, currentMs, timerStartTime,false)
    }


    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, timerStartTime: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs,it.timerStartTime,isStarted))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime*60000)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}