package com.sesac.developer_study_platform.ui.message

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
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

abstract class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(message: Message, previousMessage: Message? = null)

    fun getImageList(message: Message): Task<ListResult> {
        return Firebase.storage.reference
            .child("${message.sid}/${message.uid}/${message.timestamp}")
            .listAll()
    }
}

class MessageReceiverViewHolder(private val binding: ItemMessageReceiverBinding) :
    ViewHolder(binding) {

    override fun bind(message: Message, previousMessage: Message?) {
        binding.message = message
        binding.previousMessage = previousMessage
    }

    companion object {
        fun from(parent: ViewGroup): MessageReceiverViewHolder {
            return MessageReceiverViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_message_receiver,
                    parent,
                    false
                )
            )
        }
    }
}

class MessageSenderViewHolder(private val binding: ItemMessageSenderBinding) : ViewHolder(binding) {

    override fun bind(message: Message, previousMessage: Message?) {
        binding.message = message
        binding.previousMessage = previousMessage
    }

    companion object {
        fun from(parent: ViewGroup): MessageSenderViewHolder {
            return MessageSenderViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_message_sender,
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
        binding.message = message
        binding.previousMessage = previousMessage
        binding.rvImageList.adapter = imageAdapter

        getImageList(message).addOnSuccessListener {
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-getImageList", it.message ?: "error occurred.")
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImageReceiverViewHolder {
            return ImageReceiverViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_image_receiver,
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
        binding.message = message
        binding.previousMessage = previousMessage
        binding.rvImageList.adapter = imageAdapter

        getImageList(message).addOnSuccessListener {
            imageAdapter.submitList(it.items)
        }.addOnFailureListener {
            Log.e("MessageAdapter-getImageList", it.message ?: "error occurred.")
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImageSenderViewHolder {
            return ImageSenderViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_image_sender,
                    parent,
                    false
                )
            )
        }
    }
}