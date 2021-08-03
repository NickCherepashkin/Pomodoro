    package com.drozdova.pomodoro

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.drozdova.pomodoro.bean.Timer
import com.drozdova.pomodoro.databinding.TimerItemBinding
import com.drozdova.pomodoro.utils.*

var remTime: Long = 0

class TimerViewHolder (
    private val binding: TimerItemBinding,
    private val listener: TimerListener,
    private val resources: Resources

    ): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(timer: Timer) {
        binding.timerText.text = timer.currentMs.displayTime()
        binding.customView.setPeriod(timer.initTime)
        binding.customView.setCurrent(timer.currentMs)
        decor(timer)

        if (timer.isStarted) {
            startTimer(timer)
        } else {
            stopTimer(timer)
        }

        initButtonsListeners(timer)
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.startPauseButton.setOnClickListener {
            if (timer.isStarted) {
                listener.stop(
                    timer.id,
                    timer.initTime,
                    timer.currentMs,
                    false
                )
            } else {
                listener.start(timer.id)
            }
        }

        binding.deleteButton.setOnClickListener {
	    remTime = -1
            timer.color = R.color.white
            decor(timer)
            listener.delete(timer.id)
        }
    }

    private fun startTimer(selectTimer: Timer) {
        binding.startPauseButton.text = resources.getString(R.string.btn_stop_text)
        selectTimer.color = R.color.white
        decor(selectTimer)
        timer?.cancel()
        timer = getCountDownTimer(selectTimer)
        timer?.start()
    }

    private fun stopTimer(selectTimer: Timer) {
        timer?.cancel()
        selectTimer.isStarted = false
        binding.startPauseButton.text = resources.getString(R.string.btn_start_text)
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun decor(selectTimer: Timer) {
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timerPanel.setBackgroundColor(resources.getColor(selectTimer.color, resources.newTheme()))
            binding.deleteButton.setBackgroundColor(resources.getColor(selectTimer.color, resources.newTheme()))
        } else {
            binding.timerPanel.setBackgroundColor(Color.WHITE)
            binding.deleteButton.setBackgroundColor(Color.WHITE)
        }

    }

        private fun getCountDownTimer(selectTimer: Timer): CountDownTimer {
	        binding.customView.setPeriod(selectTimer.initTime)

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                selectTimer.currentMs -= interval
                remTime = selectTimer.currentMs

                binding.customView.setCurrent(selectTimer.currentMs)
                if (selectTimer.currentMs <= 0L) {
                    onFinish()
                }
                binding.timerText.text = selectTimer.currentMs.displayTime()
            }

            override fun onFinish() {
                selectTimer.color = R.color.purple_200
                timer?.cancel()
                binding.timerText.text = selectTimer.initTime.displayTime()
                selectTimer.currentMs = selectTimer.initTime
                decor(selectTimer)
                stopTimer(selectTimer)
            }
        }
    }
}