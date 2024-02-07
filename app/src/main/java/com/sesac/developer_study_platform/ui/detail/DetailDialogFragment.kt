package com.sesac.developer_study_platform.ui.detail

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.sesac.developer_study_platform.R

class DetailDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_detail, null)
        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<View>(R.id.btn_yes).setOnClickListener {
            val action = DetailDialogFragmentDirections.actionDetailDialogToMessage()
            findNavController().navigate(action)
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}