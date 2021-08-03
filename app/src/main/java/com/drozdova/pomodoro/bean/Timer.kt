package com.drozdova.pomodoro.bean

import androidx.annotation.ColorRes
import com.drozdova.pomodoro.R

data class Timer(
    val id: Int,                                                // id таймера
    val initTime: Long,                                         // время, заданное при создании таймера
    var currentMs: Long,                                        // текущее время (может отличаться от initTime, если нажата кнопка стоп до окончания работы таймера)
    var isStarted: Boolean,                                     // запущен или нет
    @ColorRes
    var color: Int = R.color.design_default_color_primary
)
