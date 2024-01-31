package com.sesac.developer_study_platform.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentMyPageBinding
import com.sesac.developer_study_platform.util.setImage
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val uid = Firebase.auth.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogout.setOnClickListener {
            //로그아웃 다이얼로그로 이동하기
        }
        binding.ivBookmark.setOnClickListener {
            findNavController().navigate(R.id.action_my_to_bookmark)
        }
        loadUser()
    }

    private fun loadUser() {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                uid?.let {
                    studyService.getUserById(uid)
                }
            }.onSuccess {
                if (it != null) {
                    binding.tvProfileName.text = it.userId
                    binding.ivProfileImage.setImage(it.image)
                }
            }.onFailure {
                Log.e("MyPageFragment-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}