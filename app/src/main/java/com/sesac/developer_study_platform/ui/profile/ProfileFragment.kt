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
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.data.source.remote.GithubService
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.databinding.FragmentProfileBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.util.setImage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var repositoryAdapter: RepositoryAdapter
    private val uid = Firebase.auth.uid

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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        parseJson()
        setRepositoryAdapter()
        loadUser()
    }

    private fun parseJson() {
        kotlin.runCatching {
            val assetManager = resources.assets
            val inputStream = assetManager.open("github-language-colors.json")
            val reader = inputStream.bufferedReader()
            Json.decodeFromString<Map<String, String?>>(reader.readText())
        }.onSuccess {
            repositoryAdapter = RepositoryAdapter(it)
        }.onFailure {
            Log.e("ProfileFragment", it.message ?: "error occurred.")
        }
    }

    private fun setRepositoryAdapter() {
        binding.rvRepositoryList.adapter = repositoryAdapter
        binding.rvRepositoryList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadUser() {
        val studyService = StudyService.create()
        lifecycleScope.launch {
            runCatching {
                uid?.let {
                    studyService.getUserById(uid)
                }
            }.onSuccess {
                it?.let {
                    binding.tvProfileName.text = it.userId
                    binding.ivProfileImage.setImage(it.image)
                    loadRepositoryList(it.userId)
                }
            }.onFailure {
                Log.e("ProfileFragment-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun loadRepositoryList(userId: String) {
        val githubService = GithubService.create()
        lifecycleScope.launch {
            runCatching {
                githubService.getRepositoryList(userId)
            }.onSuccess {
                repositoryAdapter.submitList(it)
            }.onFailure {
                Log.e("ProfileFragment-loadRepositoryList", it.message ?: "error occurred.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}