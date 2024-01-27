package com.sesac.developer_study_platform.ui.message

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.sesac.developer_study_platform.databinding.ItemImageBinding

class ImageAdapter : ListAdapter<StorageReference, ImageAdapter.ImageViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storageRef: StorageReference) {
            storageRef.downloadUrl.addOnSuccessListener {
                Glide.with(itemView)
                    .load(it)
                    .centerCrop()
                    .into(binding.ivImage)
            }.addOnFailureListener {
                Log.e("ImageAdapter", it.message ?: "error occurred.")
            }
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                return ImageViewHolder(
                    ItemImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<StorageReference>() {
            override fun areItemsTheSame(
                oldItem: StorageReference,
                newItem: StorageReference
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: StorageReference,
                newItem: StorageReference
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}