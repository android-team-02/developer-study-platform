package com.sesac.developer_study_platform.ui.profile

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.databinding.ItemRepositoryBinding

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
            binding.repository = repository
            setLanguage(repository, languageList)
            binding.executePendingBindings()

            Log.d("repolang", "Repository Language: ${repository.language}")
        }

        private fun setLanguage(repository: Repository, languageList: Map<String, String?>) {
            with(binding) {
                if (repository.language.isNullOrEmpty()) {
                    ivRepositoryLanguageColor.visibility = View.GONE
                    tvRepositoryLanguage.visibility = View.GONE
                } else {
                    val color = languageList.getValue(repository.language.toString())
                    ivRepositoryLanguageColor.visibility = View.VISIBLE
                    tvRepositoryLanguage.visibility = View.VISIBLE
                    ivRepositoryLanguageColor.setBackgroundColor(Color.parseColor(color))
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
