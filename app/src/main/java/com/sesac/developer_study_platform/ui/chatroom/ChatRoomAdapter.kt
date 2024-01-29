package com.sesac.developer_study_platform.ui.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.UserChatRoom
import com.sesac.developer_study_platform.databinding.ItemChatRoomBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import com.sesac.developer_study_platform.util.setImage

class ChatRoomAdapter(private val clickListener: StudyClickListener) :
    ListAdapter<UserChatRoom, ChatRoomAdapter.ChatRoomViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userChatRoom: UserChatRoom, clickListener: StudyClickListener) {
            binding.ivStudyImage.setImage(userChatRoom.image)
            binding.tvStudyName.text = userChatRoom.name
            binding.tvLastMessage.text = userChatRoom.lastMessage
            binding.tvLastMessageTime.text = userChatRoom.lastMessageTime.toString()
            binding.tvUnreadCount.text = "1"
            itemView.setOnClickListener {
                clickListener.onClick(userChatRoom.sid)
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
        val diffUtil = object : DiffUtil.ItemCallback<UserChatRoom>() {
            override fun areItemsTheSame(oldItem: UserChatRoom, newItem: UserChatRoom): Boolean {
                return oldItem.sid == newItem.sid
            }

            override fun areContentsTheSame(oldItem: UserChatRoom, newItem: UserChatRoom): Boolean {
                return oldItem == newItem
            }
        }
    }
}