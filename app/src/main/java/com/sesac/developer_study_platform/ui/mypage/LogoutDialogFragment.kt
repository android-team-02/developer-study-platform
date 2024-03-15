package com.sesac.developer_study_platform.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.local.UserPreferencesRepository
import com.sesac.developer_study_platform.databinding.DialogLogoutBinding

class LogoutDialogFragment : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LogoutDialogViewModel> {
        LogoutDialogViewModel.create(UserPreferencesRepository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        binding.btnNo.setOnClickListener {
            dismiss()
        }
        binding.btnYes.setOnClickListener {
            viewModel.startLogout()
        }
    }

    private fun setNavigation() {
        viewModel.moveToLoginEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_logout_dialog_to_login)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}