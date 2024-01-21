package com.sesac.developer_study_platform.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.databinding.ItemRepositoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepositoryAdapter : ListAdapter<Repository, RepositoryAdapter.ViewHolder>(RepositoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository)
    }

    class ViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repository: Repository) {
            binding.tvRepositoryName.text = repository.name
            binding.tvRepositoryLanguage.text = repository.language ?: "Unknown"
            binding.tvRepositoryStars.text = repository.stargazersCount.toString()
            binding.tvRepositoryFork.text = repository.forksCount.toString()
            binding.tvRepositoryIssue.text = repository.openIssuesCount.toString()

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date: Date = inputFormat.parse(repository.createdAt) ?: Date()
            binding.tvRepositoryCreatedAt.text = outputFormat.format(date)
        }
    }

    private class RepositoryDiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem == newItem
        }
    }
}


