package com.sesac.developer_study_platform.ui.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.databinding.ItemRepositoryBinding

class RepositoryAdapter(
    private val languageList: Map<String, String?>,
    private val clickListener: RepositoryClickListener,
) : ListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(currentList[position], languageList, clickListener)
    }

    class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            repository: Repository,
            languageList: Map<String, String?>,
            clickListener: RepositoryClickListener
        ) {
            binding.repository = repository
            binding.isLanguageNullOrEmpty = repository.language.isNullOrEmpty()
            binding.clickListener = clickListener
            if (!repository.language.isNullOrEmpty()) {
                val color = Color.parseColor(languageList.getValue(repository.language))
                binding.ivRepositoryLanguageColor.setBackgroundColor(color)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RepositoryViewHolder {
                return RepositoryViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_repository,
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