package com.sesac.developer_study_platform.ui.mystudy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.ui.StudyClickListener
import com.sesac.developer_study_platform.util.setImage

class MyStudyAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<UserStudy, MyStudyAdapter.MyStudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudyViewHolder {
        return MyStudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyStudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class MyStudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: UserStudy, clickListener: StudyClickListener) {
            with(binding) {
                ivStudyImage.setImage(study.image)
                tvStudyName.text = study.name
                tvStudyLanguage.text = study.language
                val daysOfWeekList = study.days.keys.toList()
                tvStudyDay.text = daysOfWeekList.joinToString(", ")
                itemView.setOnClickListener {
                    clickListener.onClick(study.sid)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyStudyViewHolder {
                return MyStudyViewHolder(
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
