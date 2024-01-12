package com.sesac.developer_study_platform

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sesac.developer_study_platform.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        val pendingResultTask = firebaseAuth.pendingAuthResult

        if (pendingResultTask == null) {
            firebaseAuth
                .startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener {
                    val uid = it.user?.uid ?: ""
                    val accessToken = "Bearer ${(it.credential as? OAuthCredential)?.accessToken.toString()}"
                    saveUserInfo(uid, accessToken)
                    tryGetUser(accessToken)
                }
                .addOnFailureListener {
                    Log.d("Login", "Error : $it")
                }
        }
    }

    private fun saveUserInfo(uid: String, accessToken: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("uid", uid)
            putString("accessToken", accessToken)
            apply()
        }
    }

    private fun tryGetUser(accessToken: String) {
        val githubService = GithubService.create()
        lifecycleScope.launch {
            kotlin.runCatching {
                githubService.getUser(accessToken)
            }.onSuccess {
                Log.d("LoginFragment", it.toString())
            }.onFailure {
                Log.e("LoginFragment", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}