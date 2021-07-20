package com.example.pomodoro.stopwatch

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var currentMsStart: Long,
    var isStarted: Boolean,
    var timerStartTime:Long,
    var isLaunched:Boolean


)