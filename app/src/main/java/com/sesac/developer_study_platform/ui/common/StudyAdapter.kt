package com.sesac.developer_study_platform.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding

class StudyAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<UserStudy, StudyAdapter.StudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        return StudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class StudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: UserStudy, clickListener: StudyClickListener) {
            binding.userStudy = study
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): StudyViewHolder {
                return StudyViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_study,
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UserStudy>() {
            override fun areItemsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
                return oldItem.sid == newItem.sid
            }

            override fun areContentsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
                return oldItem == newItem
            }
        }
    }
}