package com.sesac.developer_study_platform.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentProfileBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import kotlinx.serialization.json.Json

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var repositoryAdapter: RepositoryAdapter
    private val uid = FirebaseAuth.getInstance().uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        parseJson()
        setupRecyclerView()
        setupObservers()
        viewModel.loadUserData(uid)
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

    private fun setupRecyclerView() {
        binding.rvRepositoryList.adapter = repositoryAdapter
        binding.rvRepositoryList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvProfileName.text = it.userId
                Glide.with(this@ProfileFragment)
                    .load(it.image)
                    .into(binding.ivProfileImage)
            }
        }

        viewModel.repositories.observe(viewLifecycleOwner) { repositories ->
            repositories?.let {
                repositoryAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}