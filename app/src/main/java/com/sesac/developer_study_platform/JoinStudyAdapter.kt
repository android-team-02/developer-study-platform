package com.sesac.developer_study_platform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class JoinStudyAdapter: RecyclerView.Adapter<JoinStudyAdapter.ViewHolder>() {

    private var studies: List<Study> = listOf()

    interface OnItemClickListener {
        fun onItemClick(study: Study)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    // ViewHolder 클래스 정의
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivThumbnail: ShapeableImageView = view.findViewById(R.id.iv_thumbnail)
        private val tvStudyName: TextView = view.findViewById(R.id.tv_study_name)
        private val tvlanguage: TextView = view.findViewById(R.id.tv_language)
        private val tvcurrent: TextView = view.findViewById(R.id.tv_current_people)
        private val tvday: TextView = view.findViewById(R.id.tv_day)

        fun bind(study: Study, listener: OnItemClickListener?) {
            Glide.with(itemView)
                .load(study.image)
                .into(ivThumbnail)
            tvStudyName.text = study.name
            tvlanguage.text = study.language
            val peopleText = "${study.currentPeople}/${study.totalPeople}"
            tvcurrent.text = peopleText
            tvday.text = study.dayList.toString()

            itemView.setOnClickListener {
                listener?.onItemClick(study)
            }
        }
    }

    // onCreateViewHolder 구현
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_study_list, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder 구현
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val study = studies[position]
        holder.bind(study, onItemClickListener)
    }

    // getItemCount 구현
    override fun getItemCount(): Int {
        return studies.size
    }

    // setData 메서드 구현
    fun setData(newData: List<Study>) {
        studies = newData
        notifyDataSetChanged()
    }
}