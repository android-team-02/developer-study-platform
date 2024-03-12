package com.sesac.developer_study_platform.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.BookmarkStudy
import com.sesac.developer_study_platform.databinding.ItemBookmarkBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class BookmarkAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<BookmarkStudy, BookmarkAdapter.BookmarkViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class BookmarkViewHolder(private val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: BookmarkStudy, clickListener: StudyClickListener) {
            binding.bookmarkStudy = study
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): BookmarkViewHolder {
                return BookmarkViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_bookmark,
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