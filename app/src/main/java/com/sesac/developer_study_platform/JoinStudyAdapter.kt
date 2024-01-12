package com.sesac.developer_study_platform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sesac.developer_study_platform.databinding.ItemStudyListBinding

class JoinStudyAdapter(private val clickListener: (Study) -> Unit) :
    RecyclerView.Adapter<JoinStudyAdapter.ViewHolder>() {

    private var studies: List<Study> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val study = studies[position]
        holder.bind(study, clickListener)
    }

    override fun getItemCount(): Int = studies.size

    fun submitList(newStudies: List<Study>) {
        studies = newStudies
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemStudyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(study: Study, clickListener: (Study) -> Unit) {
            with(binding) {
                Glide.with(ivThumbnail.context)
                    .load(study.image)
                    .into(ivThumbnail)

                tvStudyName.text = study.name
                tvLanguage.text = study.language
                tvCurrentPeople.text = "${study.currentMemberCount}/${study.totalMemberCount}"
                tvDay.text = study.days.map { it.split("@")[0] }.joinToString(", ")

                root.setOnClickListener { clickListener(study) }
            }
        }
    }
}
