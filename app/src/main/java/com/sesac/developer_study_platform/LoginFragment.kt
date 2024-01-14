package com.sesac.developer_study_platform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sesac.developer_study_platform.StudyApplication.Companion.sharedPref
import com.sesac.developer_study_platform.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
                .addOnSuccessListener { onGithubLoginSuccess(it) }
                .addOnFailureListener { Log.e("LoginFragment", "Error: $it") }
        }
    }

    private fun onGithubLoginSuccess(result: AuthResult) {
        val uid = result.user?.uid.orEmpty()
        val accessToken = "Bearer ${(result.credential as OAuthCredential).accessToken}"
        saveUserInfo(uid, accessToken)
        tryGetUser(uid)
    }

    private fun saveUserInfo(uid: String, accessToken: String) {
        with(sharedPref.edit()) {
            putString(getString(R.string.all_pref_uid_key), uid)
            putString(getString(R.string.all_pref_access_token_key), accessToken)
            putBoolean(getString(R.string.all_pref_auto_login_key), true)
            apply()
        }
    }

    private fun tryGetUser(uid: String) {
        val githubService = GithubService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                githubService.getUser()
            }.onSuccess {
                tryPutUser(uid, StudyUser(it.userId, it.image))
                Log.d("LoginFragment-getUser", it.toString())
            }.onFailure {
                Log.e("LoginFragment-getUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun tryPutUser(uid: String, user: StudyUser) {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                studyService.putUser(uid, user)
            }.onSuccess {
                Log.d("LoginFragment-putUser", "success")
            }.onFailure {
                Log.e("LoginFragment-putUser", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}