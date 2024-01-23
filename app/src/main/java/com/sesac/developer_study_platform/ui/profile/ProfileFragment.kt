package com.sesac.developer_study_platform.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.source.remote.GithubService
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentProfileBinding
import com.sesac.developer_study_platform.util.setImage
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
            binding.rvRepositoryList.adapter = repositoryAdapter
            fetchUserProfileAndRepositories("kxg02ZurZAMTkgQOuNg8jN8WhiF3")
            checkMembershipInStudy("@make@abcd@time@20240111144250", firebaseUid)
        }

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchUserProfileAndRepositories(uid: String) {
        lifecycleScope.launch {
            runCatching {
                studyService.getUserById(uid)
            }.onSuccess { user ->
                binding.tvProfileName.text = user.userId
                binding.ivProfileImage.setImage(user.image)

                val repositories = GithubService.create().listRepos(user.userId)
                repositoryAdapter.submitList(repositories)

                Log.d("Repositories", "Repositories successfully loaded")
            }.onFailure { exception ->
                Log.e(
                    "fetchUserProfileAndRepositories",
                    "Error loading repositories: ${exception.message}"
                )
            }
        }
    }


    private fun checkMembershipInStudy(sid: String, firebaseUid: String) {
        lifecycleScope.launch {
            runCatching {
                studyService.getStudy(sid)
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
            binding.ivBan.visibility = View.VISIBLE
            binding.tvBan.visibility = View.VISIBLE

            binding.ivBan.setOnClickListener {
                //다이얼로그
            }

            binding.tvBan.setOnClickListener {
                //다이얼로그
            }
        } else {
            binding.ivBan.visibility = View.GONE
            binding.tvBan.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}