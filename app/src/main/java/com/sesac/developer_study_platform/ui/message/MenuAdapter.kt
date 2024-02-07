package com.sesac.developer_study_platform.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.data.StudyMembers
import com.sesac.developer_study_platform.databinding.ItemChatMenuBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.ui.studyform.DayTimeClickListener
import com.sesac.developer_study_platform.util.setImage

class MenuAdapter(private val clickListener: StudyMemberClickListener) : ListAdapter<StudyMembers, MenuAdapter.MenuViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class MenuViewHolder(private val binding: ItemChatMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studyMembers: StudyMembers, clickListener: StudyMemberClickListener) {
            binding.ivUserImage.setImage(studyMembers.studyUser.image)
            binding.tvUserId.text = studyMembers.studyUser.userId
            binding.ivAdmin.visibility = if (studyMembers.isAdmin) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                clickListener.onClick(studyMembers.userUid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MenuViewHolder {
                return MenuViewHolder(
                    ItemChatMenuBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<StudyMembers>() {
            override fun areItemsTheSame(
                oldItem: StudyMembers,
                newItem: StudyMembers
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StudyMembers,
                newItem: StudyMembers
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}