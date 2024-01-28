package com.sesac.developer_study_platform.ui.message

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.databinding.ItemImageReceiverBinding
import com.sesac.developer_study_platform.databinding.ItemImageSenderBinding
import com.sesac.developer_study_platform.databinding.ItemMessageReceiverBinding
import com.sesac.developer_study_platform.databinding.ItemMessageSenderBinding
import com.sesac.developer_study_platform.util.formatDate
import com.sesac.developer_study_platform.util.formatTime

abstract class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(message: Message, previousMessage: Message? = null)

    fun getUnreadUserCount(message: Message): Int {
        return message.totalMemberCount - message.readUsers.count()
    }

    fun getImageList(message: Message): Task<ListResult> {
        return Firebase.storage.reference
            .child("${message.sid}/${message.uid}/${message.timestamp}")
            .listAll()
    }
}

class MessageReceiverViewHolder(private val binding: ItemMessageReceiverBinding) :
    ViewHolder(binding) {

    override fun bind(message: Message, previousMessage: Message?) {
        if (previousMessage != null) {
            if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = message.timestamp.formatDate()
            } else {
                binding.flowSystemMessage.visibility = View.GONE
            }
            if (previousMessage.totalMemberCount < message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "새로운 스터디 멤버가 입장하였습니다."
            } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "스터디 멤버가 퇴장하였습니다."
            }
        } else {
            binding.flowSystemMessage.visibility = View.VISIBLE
            binding.tvSystemMessage.text = message.timestamp.formatDate()
        }
        Glide.with(itemView)
            .load(message.studyUser?.image)
            .centerCrop()
            .into(binding.ivUserImage)
        binding.ivAdmin.isVisible = message.isAdmin
        binding.tvUserId.text = message.studyUser?.userId
        binding.tvMessage.text = message.message
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (getUnreadUserCount(message) > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = getUnreadUserCount(message).toString()
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

class MessageSenderViewHolder(private val binding: ItemMessageSenderBinding) : ViewHolder(binding) {

    override fun bind(message: Message, previousMessage: Message?) {
        if (previousMessage != null) {
            if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = message.timestamp.formatDate()
            } else {
                binding.flowSystemMessage.visibility = View.GONE
            }
            if (previousMessage.totalMemberCount < message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "새로운 스터디 멤버가 입장하였습니다."
            } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "스터디 멤버가 퇴장하였습니다."
            }
        } else {
            binding.flowSystemMessage.visibility = View.VISIBLE
            binding.tvSystemMessage.text = message.timestamp.formatDate()
        }
        binding.tvMessage.text = message.message
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (getUnreadUserCount(message) > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = getUnreadUserCount(message).toString()
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

class ImageReceiverViewHolder(private val binding: ItemImageReceiverBinding) : ViewHolder(binding) {

    private val imageAdapter = ImageAdapter()

    override fun bind(message: Message, previousMessage: Message?) {
        if (previousMessage != null) {
            if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = message.timestamp.formatDate()
            } else {
                binding.flowSystemMessage.visibility = View.GONE
            }
            if (previousMessage.totalMemberCount < message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "새로운 스터디 멤버가 입장하였습니다."
            } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "스터디 멤버가 퇴장하였습니다."
            }
        } else {
            binding.flowSystemMessage.visibility = View.VISIBLE
            binding.tvSystemMessage.text = message.timestamp.formatDate()
        }
        Glide.with(itemView)
            .load(message.studyUser?.image)
            .centerCrop()
            .into(binding.ivUserImage)
        binding.ivAdmin.isVisible = message.isAdmin
        binding.tvUserId.text = message.studyUser?.userId
        getImageList(message).addOnSuccessListener {
            binding.rvImageList.adapter = imageAdapter
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
        }
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (getUnreadUserCount(message) > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = getUnreadUserCount(message).toString()
        } else {
            binding.tvUnreadUserCount.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImageReceiverViewHolder {
            return ImageReceiverViewHolder(
                ItemImageReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class ImageSenderViewHolder(private val binding: ItemImageSenderBinding) : ViewHolder(binding) {

    private val imageAdapter = ImageAdapter()

    override fun bind(message: Message, previousMessage: Message?) {
        if (previousMessage != null) {
            if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = message.timestamp.formatDate()
            } else {
                binding.flowSystemMessage.visibility = View.GONE
            }
            if (previousMessage.totalMemberCount < message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "새로운 스터디 멤버가 입장하였습니다."
            } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                binding.flowSystemMessage.visibility = View.VISIBLE
                binding.tvSystemMessage.text = "스터디 멤버가 퇴장하였습니다."
            }
        } else {
            binding.flowSystemMessage.visibility = View.VISIBLE
            binding.tvSystemMessage.text = message.timestamp.formatDate()
        }
        getImageList(message).addOnSuccessListener {
            binding.rvImageList.adapter = imageAdapter
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
        }
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (getUnreadUserCount(message) > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = getUnreadUserCount(message).toString()
        } else {
            binding.tvUnreadUserCount.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImageSenderViewHolder {
            return ImageSenderViewHolder(
                ItemImageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}