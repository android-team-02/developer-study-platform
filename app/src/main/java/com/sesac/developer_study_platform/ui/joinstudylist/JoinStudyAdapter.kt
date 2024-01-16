package com.sesac.developer_study_platform.ui.joinstudylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding

class JoinStudyAdapter(private val clickListener: (UserStudy) -> Unit) :
    ListAdapter<UserStudy, JoinStudyAdapter.ViewHolder>(UserStudyRoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userStudyRoom = getItem(position)
        holder.bind(userStudyRoom, clickListener)
    }

    class ViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userStudyRoom: UserStudy, clickListener: (UserStudy) -> Unit) {
            with(binding) {
                Glide.with(itemView)
                    .load(userStudyRoom.image)
                    .centerCrop()
                    .into(ivStudyImage)
                tvStudyName.text = userStudyRoom.name
                tvStudyLanguage.text = userStudyRoom.language
                val daysOfWeekList = userStudyRoom.days.keys.toList()
                tvStudyDay.text = daysOfWeekList.joinToString(", ")
                root.setOnClickListener { clickListener(userStudyRoom) }
            }
        }
    }

    private class UserStudyRoomDiffCallback : DiffUtil.ItemCallback<UserStudy>() {
        override fun areItemsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
            return oldItem.sid == newItem.sid
        }

        override fun areContentsTheSame(oldItem: UserStudy, newItem: UserStudy): Boolean {
            return oldItem == newItem
        }
    }
}
