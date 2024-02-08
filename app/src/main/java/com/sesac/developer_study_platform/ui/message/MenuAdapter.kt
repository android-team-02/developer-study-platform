package com.sesac.developer_study_platform.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.StudyMember
import com.sesac.developer_study_platform.databinding.ItemChatMenuBinding
import com.sesac.developer_study_platform.util.setImage

class MenuAdapter(private val clickListener: StudyMemberClickListener) :
    ListAdapter<StudyMember, MenuAdapter.MenuViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class MenuViewHolder(private val binding: ItemChatMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(studyMember: StudyMember, clickListener: StudyMemberClickListener) {
            binding.ivUserImage.setImage(studyMember.studyUser.image)
            binding.tvUserId.text = studyMember.studyUser.userId
            binding.ivAdmin.visibility = if (studyMember.isAdmin) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                clickListener.onClick(studyMember.userUid)
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
        val diffUtil = object : DiffUtil.ItemCallback<StudyMember>() {
            override fun areItemsTheSame(oldItem: StudyMember, newItem: StudyMember): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: StudyMember, newItem: StudyMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}