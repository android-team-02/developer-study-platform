package com.sesac.developer_study_platform.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.ui.StudyClickListener
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.databinding.ItemStudyBinding

class StudyAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<Study, StudyAdapter.StudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        return StudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class StudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: Study, clickListener: StudyClickListener) {
            val days = mutableListOf<String>()
            Glide.with(itemView)
                .load(study.image)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.ivStudyImage)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyPeople.text = itemView.context.getString(
                R.string.all_study_people,
                study.currentMemberCount,
                study.totalMemberCount
            )
            study.days.forEach {
                days.add(it.split("@")[0])
            }
            binding.tvStudyDay.text = days.joinToString(", ")
            itemView.setOnClickListener {
                clickListener.onClick(study)
            }
        }

        companion object {
            fun from(parent: ViewGroup): StudyViewHolder {
                return StudyViewHolder(
                    ItemStudyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Study>() {
            override fun areItemsTheSame(oldItem: Study, newItem: Study): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Study, newItem: Study): Boolean {
                return oldItem == newItem
            }
        }
    }
}