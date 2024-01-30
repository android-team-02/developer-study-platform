package com.sesac.developer_study_platform.ui.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.util.setImage

class StudyAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<UserStudy, StudyAdapter.StudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        return StudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class StudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: UserStudy, clickListener: StudyClickListener) {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("${study.sid}/${study.image}")
            imageRef.downloadUrl.addOnSuccessListener {
                binding.ivStudyImage.setImage(it.toString())
            }.addOnFailureListener {
                Log.e("StudyAdapter", it.message ?: "error occurred.")
            }
            binding.tvStudyName.text = study.name
            binding.tvStudyLanguage.text = study.language
            binding.tvStudyDay.text = study.days.joinToString(", ") { it.split(" ").first() }
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): StudyViewHolder {
                return StudyViewHolder(
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
        val diffUtil = object : DiffUtil.ItemCallback<UserStudy>() {
            override fun areItemsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
                return oldItem.sid == newItem.sid
            }

            override fun areContentsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
                return oldItem == newItem
            }
        }
    }
}