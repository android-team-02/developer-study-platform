package com.sesac.developer_study_platform.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.ui.UserStudyClickListener

class StudyAdapter(private val clickListener: UserStudyClickListener) :
    ListAdapter<UserStudy, StudyAdapter.StudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        return StudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class StudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: UserStudy, clickListener: UserStudyClickListener) {
            Glide.with(itemView)
                .load(study.image)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.ivStudyImage)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyDay.text = study.days.keys.joinToString(", ")
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