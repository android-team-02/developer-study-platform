package com.sesac.developer_study_platform.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.ui.StudyClickListener
import com.sesac.developer_study_platform.util.getAllDayList
import com.sesac.developer_study_platform.util.getDayList
import com.sesac.developer_study_platform.util.setImage

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
            binding.ivStudyImage.setImage(study.image)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyDay.text = study.days.keys.getDayList(itemView.getAllDayList())
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
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