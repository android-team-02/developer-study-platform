package com.sesac.developer_study_platform.ui.message

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.ItemImageBinding

class ImageAdapter(private val clickListener: ImageClickListener) :
    ListAdapter<StorageReference, ImageAdapter.ImageViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(currentList[position], clickListener)
    }

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storageRef: StorageReference, clickListener: ImageClickListener) {
            storageRef.downloadUrl.addOnSuccessListener {
                binding.imageUrl = it.toString()
                binding.clickListener = clickListener
            }.addOnFailureListener {
                Log.e("ImageAdapter", it.message ?: "error occurred.")
            }
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                return ImageViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_image,
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