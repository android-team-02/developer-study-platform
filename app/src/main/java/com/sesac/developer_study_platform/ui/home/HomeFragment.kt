package com.sesac.developer_study_platform.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.Category
import com.sesac.developer_study_platform.EventObserver
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.FragmentHomeBinding
import com.sesac.developer_study_platform.ui.common.SpaceItemDecoration
import com.sesac.developer_study_platform.ui.common.StudyAdapter
import com.sesac.developer_study_platform.ui.common.StudyClickListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val studyAdapter = StudyAdapter(object : StudyClickListener {
        override fun onClick(sid: String) {
            // TODO 채팅 화면으로 이동
        }
    })
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStudyAdapter()
        loadStudyList()
        setDetailButton()
        setStudyFormButton()
        with(binding) {
            setCategoryButton(tvAndroid)
            setCategoryButton(tvIos)
            setCategoryButton(tvFrontEnd)
            setCategoryButton(tvBackEnd)
            setCategoryButton(tvAi)
            setCategoryButton(tvEtc)
        }
        setNavigation()
    }

    private fun setStudyAdapter() {
        binding.rvStudyList.adapter = studyAdapter
        binding.rvStudyList.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.space_small))
        )
    }

    private fun loadStudyList() {
        viewModel.loadStudyList()
        viewModel.myStudyListEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                viewModel.insertUserStudy(it)
            }
        )
        viewModel.myStudyList.observe(viewLifecycleOwner) {
            studyAdapter.submitList(it)
        }
        viewModel.studyFormButtonEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.isMyStudyListEmpty = it
            }
        )
    }

    private fun setDetailButton() {
        binding.tvDetail.setOnClickListener {
            viewModel.moveToMyStudy()
        }
    }

    private fun setStudyFormButton() {
        binding.viewStudyForm.setOnClickListener {
            viewModel.moveToStudyForm()
        }
    }

    private fun setCategoryButton(view: TextView) {
        view.setOnClickListener {
            viewModel.moveToCategory(view.text.toString())
        }
    }

    private fun getPosition(category: String): Int {
        return if (category == getString(R.string.all_etc)) {
            Category.ETC.ordinal
        } else {
            Category.valueOf(
                category.replace("-", "").uppercase()
            ).ordinal
        }
    }

    private fun setNavigation() {
        viewModel.moveToMyStudyEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = HomeFragmentDirections.actionHomeToMyStudy()
                findNavController().navigate(action)
            }
        )
        viewModel.moveToStudyFormEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(R.id.action_home_to_study_form)
            }
        )
        viewModel.moveToCategoryEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val action = HomeFragmentDirections.actionGlobalToSearchCategory(
                    getPosition(it)
                )
                findNavController().navigate(action)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}