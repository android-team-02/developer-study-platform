package com.sesac.developer_study_platform.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.source.remote.GithubService
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val studyService = StudyService.create()
    private val repositoryAdapter = RepositoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUid = Firebase.auth.uid
        if (firebaseUid != null) {
            binding.rvRepository.adapter = repositoryAdapter
            fetchUserProfileAndRepositories("kxg02ZurZAMTkgQOuNg8jN8WhiF3")
            checkMembershipInStudy("@make@abcd@time@20240111144250", firebaseUid)
        }

        binding.toolbarArrowProfile.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchUserProfileAndRepositories(uid: String) {
        lifecycleScope.launch {
            runCatching {
                studyService.getUserById(uid)
            }.onSuccess { user ->
                binding.tvProfileName.text = user.userId
                Glide.with(binding.ivProfileImage).load(user.image).into(binding.ivProfileImage)

                val repositories = GithubService.create().listRepos(user.userId)
                repositoryAdapter.submitList(repositories)

                Log.d("Repositories", "Repositories successfully loaded")
            }.onFailure { exception ->
                Log.e("fetchUserProfileAndRepositories", "Error loading repositories: ${exception.message}")
            }
        }
    }


    private fun checkMembershipInStudy(sid: String, firebaseUid: String) {
        lifecycleScope.launch {
            runCatching {
                studyService.getDetail(sid)
            }.onSuccess { studyDetail ->
                val isLeader = studyDetail.members[firebaseUid] ?: false
                Log.d("checkMembershipInStudy", "UID: $firebaseUid, isLeader: $isLeader")
                updateUiForLeader(isLeader)
            }.onFailure { exception ->
                Log.e("checkMembershipInStudy", "Error checking membership in study", exception)
            }
        }
    }

    private fun updateUiForLeader(isLeader: Boolean) {
        if (isLeader) {
            binding.ivExport.visibility = View.VISIBLE
            binding.tvExport.visibility = View.VISIBLE

            binding.ivExport.setOnClickListener {
                //다이얼로그
            }

            binding.tvExport.setOnClickListener {
                //다이얼로그
            }
        } else {
            binding.ivExport.visibility = View.GONE
            binding.tvExport.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}