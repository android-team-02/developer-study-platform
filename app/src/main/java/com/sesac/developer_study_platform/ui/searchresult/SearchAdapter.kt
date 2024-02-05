package com.sesac.developer_study_platform.ui.searchresult

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.databinding.ItemSearchBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.setImage

class SearchAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<Study, SearchAdapter.SearchViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: Study, clickListener: StudyClickListener) {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("${study.sid}/${study.image}")
            imageRef.downloadUrl.addOnSuccessListener {
                binding.ivStudyImage.setImage(it.toString())
            }.addOnFailureListener {
                Log.e("SearchAdapter", it.message ?: "error occurred.")
            }
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyPeople.text = itemView.context.getString(
                R.string.all_study_people_format,
                study.members.count(),
                study.totalMemberCount
            )
            binding.tvStudyDay.text = study.days.joinToString("\n")
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