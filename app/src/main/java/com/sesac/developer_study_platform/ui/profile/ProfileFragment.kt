package com.sesac.developer_study_platform.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentProfileBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var repositoryAdapter: RepositoryAdapter
    private val args by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackButton()
        parseJson()
        loadUser()
        loadRepositoryList()
        setNavigation()
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun parseJson() {
        viewModel.parseJson(resources.assets)
        viewModel.languageListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                repositoryAdapter = RepositoryAdapter(it)
                setRepositoryAdapter()
            }
        )
    }

    private fun setRepositoryAdapter() {
        binding.rvRepositoryList.adapter = repositoryAdapter
        binding.rvRepositoryList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadUser() {
        viewModel.loadUser(args.uid)
        viewModel.userEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.studyUser = it
            }
        )
    }

    private fun loadRepositoryList() {
        viewModel.repositoryListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                repositoryAdapter.submitList(it)
            }
        )
    }

    private fun setNavigation() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}