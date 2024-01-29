package com.sesac.developer_study_platform.ui.studyform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.databinding.ItemDayTimeBinding

class DayTimeAdapter(private val clickListener: DayTimeClickListener) :
    ListAdapter<DayTime, DayTimeAdapter.DayTimeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTimeViewHolder {
        return DayTimeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DayTimeViewHolder, position: Int) {
        return holder.bind(currentList[position], clickListener)
    }

    class DayTimeViewHolder private constructor(private val binding: ItemDayTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(daytime: DayTime, clickListener: DayTimeClickListener) {
            binding.tvDay.text = daytime.day
            binding.tvStartTime.text = daytime.startTime ?: "00:00"
            binding.tvEndTime.text = daytime.endTime ?: "00:00"
            binding.tvStartTime.setOnClickListener {
                clickListener.onClick(true, daytime)
            }
            binding.tvEndTime.setOnClickListener {
                clickListener.onClick(false, daytime)
            }
        }

        companion object {
            fun from(parent: ViewGroup): DayTimeViewHolder {
                return DayTimeViewHolder(
                    ItemDayTimeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DayTime>() {
            override fun areItemsTheSame(oldItem: DayTime, newItem: DayTime): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: DayTime, newItem: DayTime): Boolean {
                return oldItem == newItem
            }
        }
    }
}