package ir.alirezahp.streamevideo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.alirezahp.streamevideo.databinding.BottomSheetUsersBinding

class UserSwitchBottomSheet : BottomSheetDialogFragment() {

    private var onUserSelected: ((Int) -> Unit)? = null

    companion object {
        fun newInstance(onUserSelected: (Int) -> Unit): UserSwitchBottomSheet {
            return UserSwitchBottomSheet().apply {
                this.onUserSelected = onUserSelected
            }
        }
    }

    private var _binding: BottomSheetUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnUser1.setOnClickListener {
            switchUser(1)
        }
        binding.btnUser2.setOnClickListener {
            switchUser(2)
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }


    private fun switchUser(userId: Int) {
        onUserSelected?.invoke(userId)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}