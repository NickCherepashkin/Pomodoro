package com.drozdova.pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drozdova.pomodoro.bean.Timer
import com.drozdova.pomodoro.databinding.TimerItemBinding

class TimerAdapter(private val listener: TimerListener, private val timersList: MutableList<Timer>): ListAdapter<Timer, TimerViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerItemBinding.inflate(layoutInflater, parent, false)
        return TimerViewHolder(binding, listener, binding.root.context.resources)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return timersList[position].id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return timersList[position].id
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Timer>() {

            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted &&
                        oldItem.initTime == newItem.initTime &&
                        oldItem.color == newItem.color
            }
            override fun getChangePayload(oldItem: Timer, newItem: Timer) = Any()
        }
    }
}