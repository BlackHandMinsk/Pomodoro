package com.example.pomodoro.stopwatch

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var timerStartTime:Long,
    var isStarted: Boolean
)