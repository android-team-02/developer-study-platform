package com.sesac.developer_study_platform.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkDao
import com.sesac.developer_study_platform.databinding.FragmentBookmarkBinding
import com.sesac.developer_study_platform.ui.StudyClickListener
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private val bookmarkAdapter = BookmarkAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            // 상세 화면으로 이동
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
        binding.rvStudyList.adapter = bookmarkAdapter
        loadStudyList()
    }

    private fun loadStudyList() {
        lifecycleScope.launch {
            val bookmarkStudyList = bookmarkDao.getAllBookmarkStudy()
            if (bookmarkStudyList.isNotEmpty()) {
                bookmarkAdapter.submitList(bookmarkStudyList.sortedByDescending { it.id })
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