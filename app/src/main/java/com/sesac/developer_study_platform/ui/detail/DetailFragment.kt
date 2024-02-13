package com.sesac.developer_study_platform.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackButton()
        loadStudy()
        loadBookmarkButtonState()
        setBookmarkButton()
        setJoinStudyButton()
        setNavigation()
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun loadStudy() {
        lifecycleScope.launch {
            viewModel.loadStudy(args.studyId)
        }
        viewModel.studyEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.study = it
            }
        )
        viewModel.studyMemberListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.tvMemberValue.text = it.joinToString("\n")
            }
        )
    }

    private fun loadBookmarkButtonState() {
        lifecycleScope.launch {
            binding.ivBookmark.isSelected = viewModel.isBookmarkSelected(args.studyId)
        }
    }

    private fun setBookmarkButton() {
        binding.ivBookmark.setOnClickListener {
            if (binding.ivBookmark.isSelected) {
                binding.ivBookmark.isSelected = false
                deleteBookmarkStudyBySid()
            } else {
                binding.ivBookmark.isSelected = true
                insertBookmarkStudy()
            }
        }
    }

    private fun insertBookmarkStudy() {
        lifecycleScope.launch {
            viewModel.insertBookmarkStudy(viewModel.study)
        }
    }

    private fun deleteBookmarkStudyBySid() {
        lifecycleScope.launch {
            viewModel.deleteBookmarkStudyBySid(args.studyId)
        }
    }

    private fun setJoinStudyButton() {
        binding.btnJoinStudy.setOnClickListener {
            viewModel.moveToJoinStudyDialog(args.studyId)
        }
    }

    private fun setNavigation() {
        moveToBack()
        moveToJoinStudyDialog()
    }

    private fun moveToBack() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
    }

    private fun moveToJoinStudyDialog() {
        viewModel.moveToJoinStudyDialogEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = DetailFragmentDirections.actionDetailToJoinStudyDialog(viewModel.study)
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}