package com.group29.eventapp.presentation.timecapsule

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.TimeCapsuleModel
import com.group29.eventapp.data.model.TimeCapsuleStatus
import com.group29.eventapp.databinding.FragmentCapsuleDetailBinding
import kotlinx.coroutines.launch

class CapsuleDetailFragment : Fragment() {

    private var _binding: FragmentCapsuleDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CapsuleDetailViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCapsuleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val capsuleId = arguments?.getString("capsuleId") ?: "tc_1"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupActions()
        observeViewModel()

        viewModel.loadCapsule(capsuleId)
    }

    private fun setupActions() {
        binding.btnTryOpenNow.setOnClickListener {
            viewModel.openCapsule { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRetryFile.setOnClickListener {
            binding.cardFileError.visibility = View.GONE
            Toast.makeText(requireContext(), "Tải lại tệp đính kèm thành công!", Toast.LENGTH_SHORT).show()
        }

        binding.btnReply.setOnClickListener {
            Toast.makeText(requireContext(), "Đã mở hộp thoại phản hồi thông điệp!", Toast.LENGTH_SHORT).show()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(requireContext(), "Đã sao chép liên kết chia sẻ Hộp thời gian!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.capsuleState.collect { state ->
                    if (state is UiState.Success) {
                        bindCapsule(state.data)
                    }
                }
            }
        }
    }

    private fun bindCapsule(capsule: TimeCapsuleModel) {
        val isLocked = (capsule.status == TimeCapsuleStatus.Locked || capsule.status == TimeCapsuleStatus.Sealed) &&
                System.currentTimeMillis() < capsule.unlockAt

        if (isLocked) {
            binding.layoutLockedView.visibility = View.VISIBLE
            binding.layoutUnlockedView.visibility = View.GONE

            binding.tvLockedTitle.text = capsule.title
            startCountdown(capsule.unlockAt)
        } else {
            binding.layoutLockedView.visibility = View.GONE
            binding.layoutUnlockedView.visibility = View.VISIBLE

            binding.tvUnlockedTitle.text = capsule.title
            binding.tvSenderRecipient.text = "Từ: ${capsule.creatorName} • Gửi tới: ${capsule.recipientName}"

            val createdStr = DateTimeUtils.formatDateTime(capsule.createdAt)
            val openedStr = capsule.openedAt?.let { DateTimeUtils.formatDateTime(it) } ?: "Chưa ghi nhận"
            binding.tvDatesInfo.text = "Niêm phong: $createdStr • Mở khóa: $openedStr"

            binding.tvUnlockedMessage.text = capsule.message

            // If it's AvailableToOpen (not yet marked Opened in repo), allow open
            if (capsule.status == TimeCapsuleStatus.AvailableToOpen) {
                viewModel.openCapsule { _, _ -> }
            }
        }
    }

    private fun startCountdown(unlockAt: Long) {
        countDownTimer?.cancel()
        val remaining = unlockAt - System.currentTimeMillis()
        if (remaining <= 0) {
            binding.tvLockedCountdown.text = "00:00:00 (Đã tới giờ mở khóa!)"
            return
        }

        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvLockedCountdown.text = DateTimeUtils.formatCountdown(millisUntilFinished)
            }
            override fun onFinish() {
                binding.tvLockedCountdown.text = "00:00:00 (Đã tới giờ mở khóa!)"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
