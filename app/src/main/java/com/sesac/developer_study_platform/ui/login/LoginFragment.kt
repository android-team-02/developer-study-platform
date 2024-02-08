package com.sesac.developer_study_platform.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.local.UserPreferencesRepository
import com.sesac.developer_study_platform.databinding.FragmentLoginBinding
import com.sesac.developer_study_platform.util.showSnackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.create(UserPreferencesRepository(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAutoLogin()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLoginButton()
        setLoginFailureEvent()
        setNavigation()
    }

    private fun setLoginButton() {
        binding.btnLogin.setOnClickListener {
            viewModel.startGithubLogin(requireActivity())
        }
    }

    private fun setLoginFailureEvent() {
        viewModel.loginFailureEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.root.showSnackbar(R.string.login_error)
            }
        )
    }

    private fun setNavigation() {
        viewModel.autoLoginEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                if (it) {
                    findNavController().navigate(R.id.action_login_to_home)
                }
            }
        )
        viewModel.moveToHome.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_login_to_home)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}