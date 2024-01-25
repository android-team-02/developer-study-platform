package com.sesac.developer_study_platform.ui.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.databinding.ItemRepositoryBinding
import com.sesac.developer_study_platform.util.formatDate

class RepositoryAdapter(private val languageList: Map<String, String?>) :
    ListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(currentList[position], languageList)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        fun bind(repository: Repository, languageList: Map<String, String?>) {
            binding.tvRepositoryName.text = repository.name
            if (repository.language.isNullOrEmpty()) {
                binding.ivRepositoryLanguage.visibility = View.GONE
                binding.tvRepositoryLanguage.visibility = View.GONE
            } else {
                val color = languageList.getValue(repository.language.toString())
                binding.ivRepositoryLanguage.setBackgroundColor(Color.parseColor(color))
                binding.tvRepositoryLanguage.text = repository.language
            }
            binding.tvRepositoryStar.text = repository.star.toString()
            binding.tvRepositoryFork.text = repository.fork.toString()
            binding.tvRepositoryIssue.text = repository.issue.toString()
            binding.tvRepositoryCreatedAt.text = repository.createdAt?.formatDate(pattern, pattern)
        }

        companion object {
            fun from(parent: ViewGroup): RepositoryViewHolder {
                return RepositoryViewHolder(
                    ItemRepositoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem == newItem
            }
        }
    }
}


