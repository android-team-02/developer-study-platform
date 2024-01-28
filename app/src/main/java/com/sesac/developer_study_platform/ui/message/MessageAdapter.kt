package com.sesac.developer_study_platform.ui.message

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.Message

class MessageAdapter : ListAdapter<Message, ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ViewType.MESSAGE_RECEIVER.ordinal -> MessageReceiverViewHolder.from(parent)
            ViewType.MESSAGE_SENDER.ordinal -> MessageSenderViewHolder.from(parent)
            ViewType.IMAGE_RECEIVER.ordinal -> ImageReceiverViewHolder.from(parent)
            else -> ImageSenderViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= 1) {
            holder.bind(getItem(position), getItem(position - 1))
        } else {
            holder.bind(getItem(position))
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