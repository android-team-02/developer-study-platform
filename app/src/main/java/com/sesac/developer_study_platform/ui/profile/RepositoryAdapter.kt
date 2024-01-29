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

    class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository, languageList: Map<String, String?>) {
            with(binding) {
                tvRepositoryName.text = repository.name
                setLanguage(repository, languageList)
                tvRepositoryStar.text = repository.star.toString()
                tvRepositoryFork.text = repository.fork.toString()
                tvRepositoryIssue.text = repository.issue.toString()
                tvRepositoryCreatedAt.text = repository.createdAt?.formatDate()
            }
        }

        private fun setLanguage(repository: Repository, languageList: Map<String, String?>) {
            with(binding) {
                if (repository.language.isNullOrEmpty()) {
                    ivRepositoryLanguage.visibility = View.GONE
                    tvRepositoryLanguage.visibility = View.GONE
                } else {
                    val color = languageList.getValue(repository.language.toString())
                    ivRepositoryLanguage.visibility = View.VISIBLE
                    tvRepositoryLanguage.visibility = View.VISIBLE
                    ivRepositoryLanguage.setBackgroundColor(Color.parseColor(color))
                    tvRepositoryLanguage.text = repository.language
                }
            }
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


