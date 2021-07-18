package com.example.stopwatch

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
       // stopwatchAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY //TODO пересмотреть
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
          //  adapter.notifyItemChanged()
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            startTime = binding.inputTime.text.toString().toLong()
            stopwatches.add(Stopwatch(nextId++, startTime.toLong()*60000, startTime.toLong()*60000,false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
//        binding.addNewStopwatchButton.setOnClickListener {
//            startTime = binding.inputTime.text.toString().toLong()
//            stopwatches.add(
//                Stopwatch(
//                    nextId++,
//                    startTime.toLong() * 60000,
//                    startTime.toLong() * 60000,
//                    false
//                )
//            )
//            stopwatchAdapter.submitList(stopwatches.toList())
////        }
//        }
    }

   



    @RequiresApi(Build.VERSION_CODES.N)
    override fun start(id: Int) {
      //  changeStopwatch(id, null, null,true)
            val startedWatches = arrayListOf<Int>()
            var c = 0
            stopwatches.forEach {
                if (it.isStarted) {
                    startedWatches.add(c)
                    c++
                } else {
                    c++
                }
            }

            startedWatches.forEach {
                changeStopwatch(
                    stopwatches[it].id,
                    stopwatches[it].currentMs,
                    stopwatches[it].timerStartTime,
                    false
                )
            }
            changeStopwatch(id, null, null, true)
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
        var time:Long = 0
        stopwatches.forEach{
            if(it.isStarted){
                time= it.currentMs
            }
        }
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, time)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}