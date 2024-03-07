package com.sesac.developer_study_platform.ui.common

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.local.FcmTokenRepository
import com.sesac.developer_study_platform.databinding.DialogNotificationPermissionBinding

class NotificationPermissionDialogFragment : DialogFragment() {

    private var _binding: DialogNotificationPermissionBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NotificationPermissionDialogViewModel> {
        NotificationPermissionDialogViewModel.create(FcmTokenRepository(requireContext()))
    }
    private val args by navArgs<NotificationPermissionDialogFragmentArgs>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkNotificationKey()
            } else {
                Toast.makeText(context, getString(R.string.all_notification_info), Toast.LENGTH_SHORT).show()
                viewModel.moveToMessage(args.studyId)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNotificationPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setYesButton()
        binding.btnNo.setOnClickListener {
            viewModel.moveToMessage(args.studyId)
        }
        setNavigation()
    }

    private fun setYesButton() {
        binding.btnYes.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                checkNotificationKey()
            }
        }
    }

    private fun checkNotificationKey() {
        viewModel.checkNotificationKey(args.studyId)
        viewModel.checkNotificationKeyEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                viewModel.moveToMessage(args.studyId)
            }
        )
    }

    private fun setNavigation() {
        viewModel.moveToMessageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = NotificationPermissionDialogFragmentDirections.actionGlobalToMessage(it)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}