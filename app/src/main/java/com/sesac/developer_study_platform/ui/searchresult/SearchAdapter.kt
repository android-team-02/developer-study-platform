package com.sesac.developer_study_platform.ui.searchresult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.databinding.ItemSearchBinding
import com.sesac.developer_study_platform.ui.SearchClickListener

class SearchAdapter(private val clickListener: SearchClickListener) :
    ListAdapter<Study, SearchAdapter.SearchViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: Study, clickListener: SearchClickListener) {
            Glide.with(itemView)
                .load(study.image)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.ivStudyImage)
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyPeople.text = itemView.context.getString(
                R.string.all_study_people,
                study.members.count(),
                study.totalMemberCount
            )
            binding.tvStudyDay.text = study.days.keys.joinToString(", ")
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): SearchViewHolder {
                return SearchViewHolder(
                    ItemSearchBinding.inflate(
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