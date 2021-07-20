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
import com.example.pomodoro.*
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.stopwatch.Stopwatch
import com.example.pomodoro.stopwatch.StopwatchAdapter
import com.example.pomodoro.stopwatch.StopwatchListener

class MainActivity : AppCompatActivity(),LifecycleObserver, StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0
    private var startTimeNotification = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
     //   stopwatchAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY //TODO пересмотреть
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
          //  adapter.notifyItemChanged()
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            val input = binding.inputTime.text.toString().toLong() * 60000
            stopwatches.add(Stopwatch(nextId++,
                0L,
                input,
                false,
                0L,//0L
                isLaunched = false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }




    override fun start(id: Int, timerStartTime: Long) {
        changeStopwatch(id, null, true, timerStartTime)
    }

    override fun stop(id: Int, currentMs: Long, timerStartTime: Long) {
        startTimeNotification = 0L
        changeStopwatch(id, currentMs, false, timerStartTime)
    }


    override fun delete(id: Int) {
        val timerDelete = stopwatches.find { it.id == id }
        if(timerDelete!!.isStarted) startTimeNotification = 0L
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean, startTime: Long) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.isStarted && it.id!=id){
                val currentMsNew = System.currentTimeMillis()-it.timerStartTime
                newTimers.add(
                    Stopwatch(
                        it.id,
                        currentMsNew,
                        it.currentMsStart,
                        false,
                        startTime,
                        true
                    )
                )
            } else if (it.id == id && isStarted) {
                startTimeNotification = it.currentMsStart + startTime
                println("it.currentMs + it.startTime ${it.currentMs} + ${it.timerStartTime}")
                newTimers.add(
                    Stopwatch(
                        it.id,
                        currentMs ?: it.currentMs,
                        it.currentMsStart,
                        isStarted,
                        startTime,
                        true
                    )
                )
            } else {
                newTimers.add(
                    Stopwatch(
                        it.id,
                        it.currentMs,
                        it.currentMsStart,
                        false,
                        startTime,
                        it.isLaunched
                    )
                )
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
        println("список таймеров после старт $stopwatches")
    }





//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        var time:Long = 0
//        stopwatches.forEach{
//            if(it.isStarted){
//                time= it.currentMs
//            }
//        }
//        val startIntent = Intent(this, ForegroundService::class.java)
//        startIntent.putExtra(COMMAND_ID, COMMAND_START)
//        startIntent.putExtra(STARTED_TIMER_TIME_MS, time)
//        startService(startIntent)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        val stopIntent = Intent(this, ForegroundService::class.java)
//        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
//        startService(stopIntent)
//    }
}