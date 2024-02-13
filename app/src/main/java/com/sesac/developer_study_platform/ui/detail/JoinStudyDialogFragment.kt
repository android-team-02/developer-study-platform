package com.sesac.developer_study_platform.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.databinding.DialogJoinStudyBinding

class JoinStudyDialogFragment : DialogFragment() {

    private var _binding: DialogJoinStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<JoinStudyDialogViewModel>()
    private val args by navArgs<JoinStudyDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogJoinStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialog()
        setNavigation()
        setYesButton()
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun setDialog() {
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels

        val params = dialog?.window?.attributes
        params?.width = (widthPixels * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_white_radius_18dp)
    }

    private fun setYesButton() {
        binding.btnYes.setOnClickListener {
            viewModel.addUserStudy(
                args.study.sid,
                with(args.study) {
                    UserStudy(sid, name, image, language, days, startDate, endDate)
                }
            )
            viewModel.addUserStudyEvent.observe(
                viewLifecycleOwner,
                EventObserver {
                    viewModel.moveToMessage(args.study.sid)
                }
            )
        }
    }

    private fun setNavigation() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = JoinStudyDialogFragmentDirections.actionJoinStudyDialogToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}