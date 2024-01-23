package com.sesac.developer_study_platform.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.setImage

class BookmarkAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<BookmarkStudy, BookmarkAdapter.BookmarkViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class BookmarkViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: BookmarkStudy, clickListener: StudyClickListener) {
            binding.ivStudyImage.setImage(study.image)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyDay.text = study.days
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): BookmarkViewHolder {
                return BookmarkViewHolder(
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
        val diffUtil = object : DiffUtil.ItemCallback<BookmarkStudy>() {
            override fun areItemsTheSame(oldItem: BookmarkStudy, newItem: BookmarkStudy): Boolean {
                return oldItem.sid == newItem.sid
            }

            override fun areContentsTheSame(oldItem: BookmarkStudy, newItem: BookmarkStudy): Boolean {
                return oldItem == newItem
            }
        }
    }
}