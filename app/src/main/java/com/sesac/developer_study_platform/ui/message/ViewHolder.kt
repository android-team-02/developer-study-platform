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
import com.sesac.developer_study_platform.R
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
        with(binding) {
            if (previousMessage != null) {
                if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text = message.timestamp.formatDate()
                } else {
                    flowSystemMessage.visibility = View.GONE
                }
                if (previousMessage.totalMemberCount < message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_new_study_member)
                } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_left_study_member)
                }
            } else {
                flowSystemMessage.visibility = View.VISIBLE
                tvSystemMessage.text = message.timestamp.formatDate()
            }
            Glide.with(itemView)
                .load(message.studyUser?.image)
                .centerCrop()
                .into(ivUserImage)
            ivAdmin.isVisible = message.isAdmin
            tvUserId.text = message.studyUser?.userId
            tvMessage.text = message.message
            tvTimestamp.text = message.timestamp.formatTime()
            if (getUnreadUserCount(message) > 0) {
                tvUnreadUserCount.visibility = View.VISIBLE
                tvUnreadUserCount.text = getUnreadUserCount(message).toString()
            } else {
                tvUnreadUserCount.visibility = View.GONE
            }
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
        with(binding) {
            if (previousMessage != null) {
                if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text = message.timestamp.formatDate()
                } else {
                    flowSystemMessage.visibility = View.GONE
                }
                if (previousMessage.totalMemberCount < message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_new_study_member)
                } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_left_study_member)
                }
            } else {
                flowSystemMessage.visibility = View.VISIBLE
                tvSystemMessage.text = message.timestamp.formatDate()
            }
            tvMessage.text = message.message
            tvTimestamp.text = message.timestamp.formatTime()
            if (getUnreadUserCount(message) > 0) {
                tvUnreadUserCount.visibility = View.VISIBLE
                tvUnreadUserCount.text = getUnreadUserCount(message).toString()
            } else {
                tvUnreadUserCount.visibility = View.GONE
            }
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
        with(binding) {
            if (previousMessage != null) {
                if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text = message.timestamp.formatDate()
                } else {
                    flowSystemMessage.visibility = View.GONE
                }
                if (previousMessage.totalMemberCount < message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_new_study_member)
                } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_left_study_member)
                }
            } else {
                flowSystemMessage.visibility = View.VISIBLE
                tvSystemMessage.text = message.timestamp.formatDate()
            }
            Glide.with(itemView)
                .load(message.studyUser?.image)
                .centerCrop()
                .into(ivUserImage)
            ivAdmin.isVisible = message.isAdmin
            tvUserId.text = message.studyUser?.userId
            getImageList(message).addOnSuccessListener {
                rvImageList.adapter = imageAdapter
                imageAdapter.submitList(it.items)
            }.addOnFailureListener {
                Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
            }
            tvTimestamp.text = message.timestamp.formatTime()
            if (getUnreadUserCount(message) > 0) {
                tvUnreadUserCount.visibility = View.VISIBLE
                tvUnreadUserCount.text = getUnreadUserCount(message).toString()
            } else {
                tvUnreadUserCount.visibility = View.GONE
            }
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
        with(binding) {
            if (previousMessage != null) {
                if (previousMessage.timestamp.formatDate() < message.timestamp.formatDate()) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text = message.timestamp.formatDate()
                } else {
                    flowSystemMessage.visibility = View.GONE
                }
                if (previousMessage.totalMemberCount < message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_new_study_member)
                } else if (previousMessage.totalMemberCount > message.totalMemberCount) {
                    flowSystemMessage.visibility = View.VISIBLE
                    tvSystemMessage.text =
                        itemView.context.getString(R.string.message_left_study_member)
                }
            } else {
                flowSystemMessage.visibility = View.VISIBLE
                tvSystemMessage.text = message.timestamp.formatDate()
            }
            getImageList(message).addOnSuccessListener {
                rvImageList.adapter = imageAdapter
                imageAdapter.submitList(it.items)
            }.addOnFailureListener {
                Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
            }
            tvTimestamp.text = message.timestamp.formatTime()
            if (getUnreadUserCount(message) > 0) {
                tvUnreadUserCount.visibility = View.VISIBLE
                tvUnreadUserCount.text = getUnreadUserCount(message).toString()
            } else {
                tvUnreadUserCount.visibility = View.GONE
            }
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