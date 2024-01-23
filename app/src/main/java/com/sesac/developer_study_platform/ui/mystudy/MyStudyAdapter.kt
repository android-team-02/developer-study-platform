package com.sesac.developer_study_platform.ui.mystudy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemStudyBinding
import com.sesac.developer_study_platform.ui.StudyClickListener

class MyStudyAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<UserStudy, MyStudyAdapter.MyStudyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudyViewHolder {
        return MyStudyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyStudyViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class MyStudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userStudyRoom: UserStudy, clickListener: StudyClickListener) {
            with(binding) {
                Glide.with(itemView)
                    .load(userStudyRoom.image)
                    .centerCrop()
                    .into(ivStudyImage)
                tvStudyName.text = userStudyRoom.name
                tvStudyLanguage.text = userStudyRoom.language
                val daysOfWeekList = userStudyRoom.days.keys.toList()
                tvStudyDay.text = daysOfWeekList.joinToString(", ")
                itemView.setOnClickListener {
                    clickListener.onClick(userStudyRoom.sid)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyStudyViewHolder {
                return MyStudyViewHolder(
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
