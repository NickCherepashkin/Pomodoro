package com.drozdova.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.drozdova.pomodoro.bean.Timer
import com.drozdova.pomodoro.databinding.ActivityMainBinding
import com.drozdova.pomodoro.service.ForegroundService
import com.drozdova.pomodoro.utils.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private var backPressed: Long = 0
    // список таймеров - изменяемый
    private val timersList = mutableListOf<Timer>()
    private val timerAdapter = TimerAdapter(this, timersList)
    private var nextId = 0
    var startTimerLong = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (savedInstanceState != null) {
            nextId = savedInstanceState.getInt(NEXT_ID)
            val size = savedInstanceState.getInt(SIZE_TIMERS)
            for (i in 0 until size) {
                val id = savedInstanceState.getInt("$ID$i")
                val initial = savedInstanceState.getLong("$INITIAL$i")
                val currentMs = savedInstanceState.getLong("$MS$i")
                val progress = savedInstanceState.getLong("$PROGRESS$i")
                val isStarted = savedInstanceState.getBoolean("$START$i")
                val color = savedInstanceState.getInt("$COLOR$i")
                timersList.add(Timer(id, initial, currentMs, isStarted, color))
            }
            timerAdapter.submitList(timersList.toList())
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            timerAdapter.setHasStableIds(true);
            adapter = timerAdapter
        }

        binding.addNewTimerButton.setOnClickListener {
            // считываем введенное значение
            val startTimer = binding.timeValue.text.toString()
            // 6000 - 100 часов
            if (startTimer.isNotBlank() && startTimer.toLong() < 6000) {
                startTimerLong = startTimer.toLong() * 60000L
                timersList.add(
                    Timer(
                        nextId++,
                        startTimerLong,
                        startTimerLong,
                        false,
                        R.color.white
                    )
                )
                timerAdapter.submitList(timersList.toList())

            } else Toast.makeText(this, "Введенны не коректные данные!!!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NEXT_ID, nextId)
        outState.putInt(SIZE_TIMERS, timersList.size)
        for (i in timersList.indices) {
            outState.putInt("$ID$i", timersList[i].id)
            outState.putLong("$INITIAL$i", timersList[i].initTime)
            outState.putLong("$MS$i", timersList[i].currentMs)
            outState.putBoolean("$START$i", timersList[i].isStarted)
            outState.putInt("$COLOR$i", timersList[i].color)
        }
    }

    override fun start(id: Int) {
        changeTimer(id, null, null,  true)
    }

    override fun stop(id: Int, initTime: Long, currentMs: Long, isStarted: Boolean) {
        changeTimer(id, initTime, currentMs, isStarted)
    }

    override fun delete(id: Int) {
        timersList.remove(timersList.find { it.id == id })
        timerAdapter.submitList(timersList.toList())
    }

    private fun changeTimer(id: Int,
                            initTime: Long?,
                            currentMs: Long?,
                            isStarted: Boolean) {
        var index = 0
        timersList.forEach {
            if (it.id == id) {
                timersList[index] = Timer(it.id, initTime ?: it.initTime, currentMs ?: it.currentMs, isStarted = isStarted)
            } else if (timersList[index].isStarted) {
                timersList[index] = Timer(it.id, initTime ?: it.initTime, currentMs ?: it.currentMs, isStarted = false)
            }
            index++
        }
        timerAdapter.submitList(timersList.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val time = timersList.find { it.isStarted }
        if (time != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, remTime)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {

            super.onBackPressed()
            exitProcess(0)
        } else {
            Toast.makeText(baseContext, "Нажмите дважды, чтобы выйти!", Toast.LENGTH_SHORT).show()
        }
        backPressed = System.currentTimeMillis()
    }
}