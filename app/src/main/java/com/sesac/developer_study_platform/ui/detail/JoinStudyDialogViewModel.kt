package com.sesac.developer_study_platform.ui.detail


import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.sesac.developer_study_platform.Event

class JoinStudyDialogViewModel : ViewModel() {

    private val _dismissDialogEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val dismissDialogEvent: LiveData<Event<Unit>> = _dismissDialogEvent

    fun onYesButtonClicked(fragment: Fragment, sid: String) {
        val action = JoinStudyDialogFragmentDirections.actionDestJoinStudyDialogToDestMessage(sid)
        findNavController(fragment).navigate(action)
        dismissDialog()
    }

    private fun dismissDialog() {
        _dismissDialogEvent.value = Event(Unit)
    }
}