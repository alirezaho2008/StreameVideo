package ir.alirezahp.streamevideo.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.alirezahp.streamevideo.R

class UserSwitchBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.bottom_sheet_users, container, false)
        view.findViewById<Button>(R.id.btnUser1).setOnClickListener { switchUser(1) }
        view.findViewById<Button>(R.id.btnUser2).setOnClickListener { switchUser(2) }
        return view
    }

    private fun switchUser(userId: Int) {
        // TODO save UserId
        dismiss()
    }
}