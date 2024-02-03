package com.sesac.developer_study_platform.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentBookmarkBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyClickListener
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private val bookmarkAdapter = BookmarkAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            val action = BookmarkFragmentDirections.actionGlobalToDetail(sid)
            findNavController().navigate(action)
        }
    })
    private val viewModel by viewModels<BookmarkViewModel>()

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
    }

    private fun setBookmarkAdapter() {
        binding.rvStudyList.adapter = bookmarkAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        lifecycleScope.launch {
            viewModel.loadStudyList()
        }
        viewModel.bookmarkStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                bookmarkAdapter.submitList(it)
            }
        )
        viewModel.emptyStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.isBookmarkStudyListEmpty = it
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}