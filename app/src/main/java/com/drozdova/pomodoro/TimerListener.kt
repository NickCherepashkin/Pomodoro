package com.drozdova.pomodoro

interface TimerListener {
    fun start(id: Int)
    fun stop(id: Int, initTime:Long, currentMs: Long, isStarted: Boolean)
    fun delete(id: Int)
}