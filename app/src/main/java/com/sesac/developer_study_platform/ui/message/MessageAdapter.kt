package com.sesac.developer_study_platform.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.databinding.ItemMessageReceiverBinding
import com.sesac.developer_study_platform.databinding.ItemMessageSenderBinding
import com.sesac.developer_study_platform.util.formatTime

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.RECEIVER.ordinal -> MessageReceiverViewHolder.from(parent)
            else -> MessageSenderViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageReceiverViewHolder -> holder.bind(getItem(position))
            is MessageSenderViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messageUid = getItem(position).uid
        val uid = Firebase.auth.uid

        return if (messageUid == uid) {
            ViewType.SENDER.ordinal
        } else {
            ViewType.RECEIVER.ordinal
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class MessageReceiverViewHolder(private val binding: ItemMessageReceiverBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        val count = message.totalMemberCount - message.readUsers.count()

        Glide.with(itemView)
            .load(message.userImage)
            .centerCrop()
            .into(binding.ivUserImage)
        binding.ivAdmin.isVisible = message.isAdmin
        binding.tvUserId.text = message.userId
        binding.tvMessage.text = message.message
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (count > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = count.toString()
        } else {
            binding.tvUnreadUserCount.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): MessageReceiverViewHolder {
            return MessageReceiverViewHolder(
                ItemMessageReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class MessageSenderViewHolder(private val binding: ItemMessageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        val count = message.totalMemberCount - message.readUsers.count()

        binding.tvMessage.text = message.message
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (count > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = count.toString()
        } else {
            binding.tvUnreadUserCount.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): MessageSenderViewHolder {
            return MessageSenderViewHolder(
                ItemMessageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}