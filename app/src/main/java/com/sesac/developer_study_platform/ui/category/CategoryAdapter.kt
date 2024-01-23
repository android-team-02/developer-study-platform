package com.sesac.developer_study_platform.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.databinding.ItemStudyCategoryBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.setImage

class CategoryAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<Study, CategoryAdapter.CategoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class CategoryViewHolder(private val binding: ItemStudyCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: Study, clickListener: StudyClickListener) {
            binding.ivStudyImage.setImage(study.image)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyPeople.text = itemView.context.getString(
                R.string.all_study_people_format,
                study.members.count(),
                study.totalMemberCount
            )
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): CategoryViewHolder {
                return CategoryViewHolder(
                    ItemStudyCategoryBinding.inflate(
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
                return oldItem.sid == newItem.sid
            }

            override fun areContentsTheSame(oldItem: Study, newItem: Study): Boolean {
                return oldItem == newItem
            }
        }
    }
}