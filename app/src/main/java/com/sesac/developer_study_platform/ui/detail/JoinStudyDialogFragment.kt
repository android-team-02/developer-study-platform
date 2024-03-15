package com.sesac.developer_study_platform.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.local.FcmTokenRepository
import com.sesac.developer_study_platform.databinding.DialogJoinStudyBinding

class JoinStudyDialogFragment : DialogFragment() {

    private var _binding: DialogJoinStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<JoinStudyDialogViewModel> {
        JoinStudyDialogViewModel.create(FcmTokenRepository(requireContext()))
    }
    private val args by navArgs<JoinStudyDialogFragmentArgs>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                updateStudyGroup()
            } else {
                Toast.makeText(context, getString(R.string.all_notification_info), Toast.LENGTH_SHORT).show()
                viewModel.moveToMessage(args.study.sid)
            }
        }

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

        setNavigation()
        setYesButton()
        binding.btnNo.setOnClickListener {
            dismiss()
        }
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
                    askNotificationPermission()
                }
            )
        }
    }

    private fun setNavigation() {
        moveToMessage()
        moveToNotificationPermissionDialog()
    }

    private fun moveToMessage() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = JoinStudyDialogFragmentDirections.actionJoinStudyDialogToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    private fun moveToNotificationPermissionDialog() {
        viewModel.moveToNotificationPermissionDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = JoinStudyDialogFragmentDirections.actionGlobalToNotificationPermissionDialog(it)
                findNavController().navigate(action)
            }
        )
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    updateStudyGroup()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    viewModel.moveToNotificationPermissionDialog(args.study.sid)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            updateStudyGroup()
        }
    }

    private fun updateStudyGroup() {
        viewModel.updateStudyGroup(args.study.sid)
        viewModel.updateStudyGroupEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                viewModel.moveToMessage(args.study.sid)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}