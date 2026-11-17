package com.group29.eventapp.presentation.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.group29.eventapp.R
import com.group29.eventapp.data.model.TimeCapsuleType
import com.group29.eventapp.databinding.FragmentCapsuleCreateBinding
import java.util.concurrent.TimeUnit

class CapsuleCreateFragment : Fragment() {

    private var _binding: FragmentCapsuleCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CapsuleCreateViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCapsuleCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAttachMedia.setOnClickListener {
            Toast.makeText(requireContext(), "Đã đính kèm ảnh kỷ niệm (Mô phỏng)", Toast.LENGTH_SHORT).show()
        }

        binding.btnSealCapsule.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()
            val recipient = binding.etRecipient.text.toString().trim()

            if (title.isEmpty()) {
                binding.tilTitle.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }
            binding.tilTitle.error = null

            if (message.isEmpty()) {
                binding.tilMessage.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }
            binding.tilMessage.error = null

            val type = when (binding.radioGroupCapsuleType.checkedRadioButtonId) {
                R.id.rbTypeFriend -> TimeCapsuleType.Friend
                R.id.rbTypeCommunity -> TimeCapsuleType.Community
                else -> TimeCapsuleType.Personal
            }

            // Warning dialog on seal: "Khi đã niêm phong, bạn sẽ không thể chỉnh sửa hoặc xóa nội dung cho tới khi mở khóa."
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Niêm phong Hộp thời gian?")
                .setMessage(getString(R.string.capsule_seal_warning))
                .setPositiveButton("Niêm phong ngay") { _, _ ->
                    // Set unlockAt: 3 days in future (must be > now)
                    val unlockAt = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)

                    viewModel.createAndSealCapsule(
                        title = title,
                        message = message,
                        recipient = recipient,
                        type = type,
                        isPublic = binding.switchPrivacy.isChecked,
                        unlockAt = unlockAt
                    ) { success, msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        if (success) {
                            // Navigate to CapsuleVaultFragment (Màn 28)
                            findNavController().navigate(R.id.capsuleVaultFragment)
                        }
                    }
                }
                .setNegativeButton(R.string.btn_cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
