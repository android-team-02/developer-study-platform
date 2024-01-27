package com.sesac.developer_study_platform.ui.message

import android.util.Log
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
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.databinding.ItemImageReceiverBinding
import com.sesac.developer_study_platform.databinding.ItemImageSenderBinding
import com.sesac.developer_study_platform.databinding.ItemMessageReceiverBinding
import com.sesac.developer_study_platform.databinding.ItemMessageSenderBinding
import com.sesac.developer_study_platform.util.formatDate
import com.sesac.developer_study_platform.util.formatTime

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.MESSAGE_RECEIVER.ordinal -> MessageReceiverViewHolder.from(parent)
            ViewType.MESSAGE_SENDER.ordinal -> MessageSenderViewHolder.from(parent)
            ViewType.IMAGE_RECEIVER.ordinal -> ImageReceiverViewHolder.from(parent)
            else -> ImageSenderViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageReceiverViewHolder -> {
                if (position >= 1) {
                    holder.bind(getItem(position), getItem(position - 1))
                } else {
                    holder.bind(getItem(position))
                }
            }

            is MessageSenderViewHolder -> {
                if (position >= 1) {
                    holder.bind(getItem(position), getItem(position - 1))
                } else {
                    holder.bind(getItem(position))
                }
            }

            is ImageReceiverViewHolder -> {
                if (position >= 1) {
                    holder.bind(getItem(position), getItem(position - 1))
                } else {
                    holder.bind(getItem(position))
                }
            }

            is ImageSenderViewHolder -> {
                if (position >= 1) {
                    holder.bind(getItem(position), getItem(position - 1))
                } else {
                    holder.bind(getItem(position))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messageUid = getItem(position).uid
        val uid = Firebase.auth.uid
        val type = getItem(position).type

        return when (messageUid) {
            uid -> {
                if (type == ViewType.MESSAGE) {
                    ViewType.MESSAGE_SENDER.ordinal
                } else {
                    ViewType.IMAGE_SENDER.ordinal
                }
            }

            else -> {
                if (type == ViewType.MESSAGE) {
                    ViewType.MESSAGE_RECEIVER.ordinal
                } else {
                    ViewType.IMAGE_RECEIVER.ordinal
                }
            }
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

    fun bind(message: Message, previousMessage: Message? = null) {
        val count = message.totalMemberCount - message.readUsers.count()

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

    fun bind(message: Message, previousMessage: Message? = null) {
        val count = message.totalMemberCount - message.readUsers.count()

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

class ImageReceiverViewHolder(private val binding: ItemImageReceiverBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val imageAdapter = ImageAdapter()
    private val storageRef = Firebase.storage.reference

    fun bind(message: Message, previousMessage: Message? = null) {
        val count = message.totalMemberCount - message.readUsers.count()
        val listRef = storageRef.child("${message.sid}/${message.uid}/${message.timestamp}")

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
        listRef.listAll().addOnSuccessListener {
            binding.rvImageList.adapter = imageAdapter
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
        }
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (count > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = count.toString()
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

class ImageSenderViewHolder(private val binding: ItemImageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val imageAdapter = ImageAdapter()
    private val storageRef = Firebase.storage.reference

    fun bind(message: Message, previousMessage: Message? = null) {
        val count = message.totalMemberCount - message.readUsers.count()
        val listRef = storageRef.child("${message.sid}/${message.uid}/${message.timestamp}")

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
        listRef.listAll().addOnSuccessListener {
            binding.rvImageList.adapter = imageAdapter
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-listAll", it.message ?: "error occurred.")
        }
        binding.tvTimestamp.text = message.timestamp.formatTime()
        if (count > 0) {
            binding.tvUnreadUserCount.visibility = View.VISIBLE
            binding.tvUnreadUserCount.text = count.toString()
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