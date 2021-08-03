package com.drozdova.pomodoro.utils

import android.content.res.Resources

const val START_TIME = "00:00:00:00"
const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME"

const val NEXT_ID = "NEXT_ID"
const val SIZE_TIMERS = "SIZE_TIMERS"
const val ID = "ID"
const val MS = "MS"
const val INITIAL = "INITIAL"
const val PROGRESS = "PROGRESS"
const val START = "START"
const val COLOR = "COLOR"

const val UNIT_TEN_MS = 10L
const val PERIOD  = 1000L * 60L * 60L * 24L

const val FILL = 0

fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    val ms = this % 1000 / 10

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}