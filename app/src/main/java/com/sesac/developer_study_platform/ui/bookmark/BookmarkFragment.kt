package com.sesac.developer_study_platform.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentBookmarkBinding
import com.sesac.developer_study_platform.isNetworkConnected
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BookmarkViewModel>()
    private val bookmarkAdapter = BookmarkAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            viewModel.moveToDetail(sid)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackButton()
        setNavigation()
        setBookmarkAdapter()
        loadStudyList()
        networkStatus()
    }

    private fun setBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.moveToBack()
        }
    }

    private fun setNavigation() {
        viewModel.moveToBackEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            }
        )
        viewModel.moveToDetailEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = BookmarkFragmentDirections.actionGlobalToDetail(it)
                findNavController().navigate(action)
            }
        )
    }

    private fun setBookmarkAdapter() {
        binding.rvStudyList.adapter = bookmarkAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        viewModel.loadStudyList()
        viewModel.bookmarkStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                bookmarkAdapter.submitList(it)
            }
        )
        viewModel.isStudyListEmptyEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.isBookmarkStudyListEmpty = it
            }
        )
    }

    private fun networkStatus() {
        if (!isNetworkConnected(requireContext())) {
            binding.networkStatus.visibility = View.VISIBLE
        } else {
            binding.networkStatus.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}