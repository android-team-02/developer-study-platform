package com.sesac.developer_study_platform.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkDao
import com.sesac.developer_study_platform.databinding.FragmentBookmarkBinding
import com.sesac.developer_study_platform.ui.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.StudyClickListener
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBookmarkAdapter()
        loadStudyList()
    }

    private fun setBookmarkAdapter() {
        binding.rvStudyList.adapter = bookmarkAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        lifecycleScope.launch {
            val bookmarkStudyList = bookmarkDao.getAllBookmarkStudy()
            if (bookmarkStudyList.isNotEmpty()) {
                bookmarkAdapter.submitList(bookmarkStudyList)
            } else {
                binding.rvStudyList.visibility = View.GONE
                binding.groupNoData.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}