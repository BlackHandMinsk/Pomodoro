package com.example.pomodoro.stopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.pomodoro.databinding.StopwatchItemBinding
import java.sql.Time

class StopwatchAdapter(
    private val listener: StopwatchListener,
    private var positionOf:Int
) : ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)
        return StopwatchViewHolder(binding, listener, binding.root.context.resources)
    }



    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        if (getItem(position).isStarted) {
            positionOf = position
            holder.bind(getItem(position))
        }else{
            holder.bind(getItem(position))
        }
    }



    override fun onViewDetachedFromWindow(holder: StopwatchViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (getItem(positionOf).isStarted){
            println("pos"+positionOf)
            holder.setIsRecyclable(false)
        }
    }

    override fun onViewAttachedToWindow(holder: StopwatchViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (!holder.isRecyclable) {
            holder.setIsRecyclable(true)
        }
    }


    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) = Any()
        }
    }
}