package com.sesac.developer_study_platform.ui

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
    private lateinit var studyService: StudyService
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

        val firebaseUid = getCurrentFirebaseUserId()
        if (firebaseUid != null) {
            studyService = StudyService.create()
            fetchUserProfileAndRepositories("kxg02ZurZAMTkgQOuNg8jN8WhiF3")
            binding.rvRepository.adapter = repositoryAdapter
            checkMembershipInStudy("@make@abcd@time@20240111144250", firebaseUid)
        }

        binding.toolbarArrowProfile.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getCurrentFirebaseUserId(): String? {
        return Firebase.auth.uid
    }

    private fun checkMembershipInStudy(sid: String, firebaseUid: String) {
        lifecycleScope.launch {
            runCatching {
                val studyDetail = studyService.getDetail(sid)
                val isLeader = studyDetail.members[firebaseUid] ?: false
                Pair(isLeader, null)
            }.onSuccess { (isLeader, _) ->
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

    private fun fetchUserProfileAndRepositories(uid: String) {
        lifecycleScope.launch {
            runCatching {
                studyService.getUserById(uid)
            }.onSuccess { user ->
                binding.tvProfileName.text = user.userId
                Glide.with(this@ProfileFragment).load(user.image).into(binding.ivProfileImage)

                val repositories = GithubService.create().listRepos(user.userId)
                repositoryAdapter.submitList(repositories)

                Log.d("Repositories", "Repositories successfully loaded")
            }.onFailure { exception ->
                Log.e("fetchUserProfileAndRepositories", "Error loading repositories: ${exception.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}