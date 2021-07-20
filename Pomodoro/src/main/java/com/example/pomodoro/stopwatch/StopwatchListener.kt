package com.example.pomodoro.stopwatch

interface StopwatchListener {

    fun start(id: Int, timerStartTime: Long)

    fun stop(id: Int, currentMs: Long, timerStartTime:Long)

    fun delete(id: Int)
}