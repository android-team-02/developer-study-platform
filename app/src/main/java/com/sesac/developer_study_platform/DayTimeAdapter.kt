package com.sesac.developer_study_platform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.databinding.ItemDayTimeBinding

class DayTimeAdapter(private val daytimeList: List<DayTime>, private val dayTimeClickListener: DayTimeClickListener) :
    RecyclerView.Adapter<DayTimeAdapter.DayTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTimeViewHolder {
        return DayTimeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DayTimeViewHolder, position: Int) {
        return holder.bind(daytimeList[position], dayTimeClickListener)
    }

    override fun getItemCount(): Int {
        return daytimeList.size
    }

    class DayTimeViewHolder private constructor(private val binding: ItemDayTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(daytime: DayTime, dayTimeClickListener: DayTimeClickListener) {
            setDayTimeInfo(daytime, dayTimeClickListener)
        }

        private fun setDayTimeInfo(daytime: DayTime, dayTimeClickListener: DayTimeClickListener) {
            binding.tvDay.text = daytime.day
            binding.tvStartTime.text = daytime.startTime ?: "00:00"
            binding.tvEndTime.text = daytime.endTime ?: "00:00"
            binding.tvStartTime.setOnClickListener {
                dayTimeClickListener.onClick(daytime, true)
            }

            binding.tvEndTime.setOnClickListener {
                dayTimeClickListener.onClick(daytime, false)
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
}