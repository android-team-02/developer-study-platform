import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sesac.developer_study_platform.R

class BanDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_ban, null)
        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<View>(R.id.btn_yes).setOnClickListener {
            //채팅화면으로 이동
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

}
