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
            binding.dayTime = daytime
            binding.clickListener = clickListener
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