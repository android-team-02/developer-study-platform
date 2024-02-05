package com.sesac.developer_study_platform.ui.chatroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemChatRoomBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.formatTime
import com.sesac.developer_study_platform.util.setImage

class ChatRoomAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<Pair<UserStudy, ChatRoom>, ChatRoomAdapter.ChatRoomViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<UserStudy, ChatRoom>, clickListener: StudyClickListener) {
            // TODO 마지막 메시지가 이미지 일 때, 텍스트 일 때 나눠서 처리
            // TODO 마지막 메시지 타임스탬프가 오늘이 안지났으면 시간, 지났으면 날짜로 처리
            val study = pair.first
            val chatRoom = pair.second
            binding.ivStudyImage.setImage(study.image)
            binding.tvStudyName.text = study.name
            binding.tvLastMessage.text = chatRoom.lastMessage.message
            binding.tvLastMessageTime.text = chatRoom.lastMessage.timestamp.formatTime()
            val unreadCount = chatRoom.unreadUsers.getOrDefault(Firebase.auth.uid, 0)
            if (unreadCount > 0) {
                binding.tvUnreadCount.visibility = View.VISIBLE
                binding.tvUnreadCount.text = unreadCount.toString()
            } else {
                binding.tvUnreadCount.visibility = View.GONE
            }
            itemView.setOnClickListener {
                clickListener.onClick(study.sid)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ChatRoomViewHolder {
                return ChatRoomViewHolder(
                    ItemChatRoomBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Pair<UserStudy, ChatRoom>>() {
            override fun areItemsTheSame(
                oldItem: Pair<UserStudy, ChatRoom>,
                newItem: Pair<UserStudy, ChatRoom>
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: Pair<UserStudy, ChatRoom>,
                newItem: Pair<UserStudy, ChatRoom>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}