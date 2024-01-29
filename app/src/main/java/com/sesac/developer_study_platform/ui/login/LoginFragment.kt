package com.sesac.developer_study_platform.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.StudyApplication.Companion.sharedPref
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.source.remote.GithubService
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentLoginBinding
import com.sesac.developer_study_platform.util.showSnackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val hasAutoLogin = sharedPref.getBoolean(getString(R.string.login_auto_login_key), false)
        if (hasAutoLogin) {
            findNavController().navigate(R.id.action_login_to_home)
        }
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

        binding.btnLogin.setOnClickListener {
            startGithubLogin()
        }
    }

    private fun startGithubLogin() {
        val provider = OAuthProvider.newBuilder("github.com")
        val firebaseAuth = Firebase.auth

        if (firebaseAuth.pendingAuthResult == null) {
            firebaseAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener {
                    val accessToken = "Bearer ${(it.credential as OAuthCredential).accessToken}"
                    saveUserInfo(accessToken)
                    loadUser()
                }
                .addOnFailureListener {
                    binding.root.showSnackbar(R.string.login_error)
                    Log.e("LoginFragment", it.message ?: "error occurred.")
                }
        }
    }

    private fun saveUserInfo(accessToken: String) {
        with(sharedPref.edit()) {
            putString(getString(R.string.all_access_token_key), accessToken)
            putBoolean(getString(R.string.login_auto_login_key), true)
            apply()
        }
    }

    private fun loadUser() {
        val githubService = GithubService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                githubService.getUser()
            }.onSuccess {
                saveUser(StudyUser(it.userId, it.image))
            }.onFailure {
                Log.e("LoginFragment-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun saveUser(user: StudyUser) {
        val studyService = StudyService.create()
        val uid = Firebase.auth.uid

        lifecycleScope.launch {
            kotlin.runCatching {
                if (uid != null) {
                    studyService.putUser(uid, user)
                }
            }.onSuccess {
                findNavController().navigate(R.id.action_login_to_home)
            }.onFailure {
                Log.e("LoginFragment-saveUser", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}