package com.sesac.developer_study_platform.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.DialogJoinStudyBinding

class JoinStudyDialogFragment : DialogFragment() {

    private var _binding: DialogJoinStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<JoinStudyDialogViewModel>()
    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogJoinStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialogSize()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_white_radius_18dp)
        setNavigation()

        binding.btnNo.setOnClickListener {
            dismiss()
        }

        binding.btnYes.setOnClickListener {
            val sid = args.studyId
            viewModel.onYesButtonClicked(this, sid)
        }
    }

    private fun setDialogSize() {
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels

        val params = dialog?.window?.attributes
        params?.width = (widthPixels * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setNavigation() {
        viewModel.dismissDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                dismiss()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}