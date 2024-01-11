package com.sesac.developer_study_platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView

class DetailFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val backButton = view.findViewById<ShapeableImageView>(R.id.iv_arrow_detail)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }
}