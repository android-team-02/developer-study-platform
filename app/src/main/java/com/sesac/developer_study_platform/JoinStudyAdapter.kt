package com.sesac.developer_study_platform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.databinding.ItemStudyListBinding

class JoinStudyAdapter(private val clickListener: (UserStudyRoom) -> Unit) :
    ListAdapter<UserStudyRoom, JoinStudyAdapter.ViewHolder>(UserStudyRoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userStudyRoom = getItem(position)
        holder.bind(userStudyRoom, clickListener)
    }

    class ViewHolder(private val binding: ItemStudyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userStudyRoom: UserStudyRoom, clickListener: (UserStudyRoom) -> Unit) {
            with(binding) {
                Glide.with(ivThumbnail.context)
                    .load(userStudyRoom.image)
                    .into(ivThumbnail)
                tvStudyName.text = userStudyRoom.name
                tvLanguage.text = userStudyRoom.language
                val daysOfWeekList = userStudyRoom.days.keys.toList()
                tvDay.text = daysOfWeekList.joinToString(", ")
                root.setOnClickListener { clickListener(userStudyRoom) }
            }
        }
    }

    private class UserStudyRoomDiffCallback : DiffUtil.ItemCallback<UserStudyRoom>() {
        override fun areItemsTheSame(oldItem: UserStudyRoom, newItem: UserStudyRoom): Boolean {
            return oldItem.sid == newItem.sid
        }

        override fun areContentsTheSame(oldItem: UserStudyRoom, newItem: UserStudyRoom): Boolean {
            return oldItem == newItem
        }
    }
}
