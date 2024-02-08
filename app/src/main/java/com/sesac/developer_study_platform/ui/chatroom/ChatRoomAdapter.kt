package com.sesac.developer_study_platform.ui.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.ItemChatRoomBinding
import com.sesac.developer_study_platform.ui.common.StudyClickListener

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
            binding.userStudy = pair.first
            binding.chatRoom = pair.second
            binding.unreadCount = pair.second.unreadUsers.getOrDefault(Firebase.auth.uid, 0)
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ChatRoomViewHolder {
                return ChatRoomViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_chat_room,
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