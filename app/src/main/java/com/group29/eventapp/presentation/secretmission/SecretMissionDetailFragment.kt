package com.group29.eventapp.presentation.secretmission

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
import com.group29.eventapp.data.model.MissionAssignmentStatus
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.databinding.FragmentSecretMissionDetailBinding
import kotlinx.coroutines.launch

class SecretMissionDetailFragment : Fragment() {

    private var _binding: FragmentSecretMissionDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecretMissionDetailViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSecretMissionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val missionId = arguments?.getString("missionId") ?: "sm_1"
        val eventId = arguments?.getString("eventId") ?: "event_2"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupActions()
        observeViewModel()

        viewModel.loadMission(missionId, eventId)
    }

    private fun setupActions() {
        binding.btnUnlockHint.setOnClickListener {
            viewModel.unlockHint { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMockScanQr.setOnClickListener {
            binding.etEvidence.setText("QR_CODE_AI_HIDDEN_BOOTH_NPU_999")
            Toast.makeText(requireContext(), "Mô phỏng: Quét thành công mã QR ẩn!", Toast.LENGTH_SHORT).show()
        }

        binding.btnMockUploadPhoto.setOnClickListener {
            binding.etEvidence.setText("PHOTO_EVIDENCE_ATTACHED_01.JPG (Ảnh chụp tại khu vực trưng bày robot)")
            Toast.makeText(requireContext(), "Mô phỏng: Đã tải lên ảnh chụp minh chứng!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSubmitEvidence.setOnClickListener {
            val text = binding.etEvidence.text.toString().trim()
            if (text.isEmpty()) {
                binding.tilEvidence.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }
            binding.tilEvidence.error = null
            viewModel.submitEvidence(text) { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClaimReward.setOnClickListener {
            viewModel.claimReward { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.missionState.collect { state ->
                    if (state is UiState.Success) {
                        bindMission(state.data)
                    }
                }
            }
        }
    }

    private fun bindMission(mission: SecretMissionModel) {
        binding.tvMissionTitle.text = mission.title
        binding.chipReward.text = "+${mission.rewardPoints} EXP"
        binding.tvMissionDesc.text = mission.description
        binding.tvBadgeReward.text = "Phần thưởng huy hiệu: ${mission.badgeName}"

        // Countdown timer
        startCountdown(mission.expiresAt)

        // Progressive Hints
        val hints = mission.hints
        val unlockedCount = mission.unlockedHintsCount

        binding.tvHintLevel1.text = if (unlockedCount >= 1 && hints.isNotEmpty()) {
            "Gợi ý 1: ${hints[0]}"
        } else {
            "Gợi ý 1: [Bị khóa - Bấm Mở gợi ý để xem]"
        }

        binding.tvHintLevel2.text = if (unlockedCount >= 2 && hints.size >= 2) {
            "Gợi ý 2: ${hints[1]}"
        } else {
            "Gợi ý 2: [Bị khóa - Cần mở gợi ý 1 trước]"
        }

        binding.tvHintLevel3.text = if (unlockedCount >= 3 && hints.size >= 3) {
            "Gợi ý 3 (Chính xác): ${hints[2]}"
        } else {
            "Gợi ý 3 (Vị trí chính xác): [Bị khóa]"
        }

        binding.btnUnlockHint.isEnabled = unlockedCount < hints.size

        // Evidence submission state
        if (mission.assignmentStatus == MissionAssignmentStatus.PendingVerification) {
            binding.btnSubmitEvidence.isEnabled = false
            binding.btnSubmitEvidence.text = "Đang chờ Ban tổ chức duyệt"
            binding.etEvidence.setText(mission.evidenceSubmitted)
            binding.etEvidence.isEnabled = false
        } else if (mission.assignmentStatus == MissionAssignmentStatus.Completed) {
            binding.btnSubmitEvidence.isEnabled = false
            binding.btnSubmitEvidence.text = "Đã duyệt thành công"
            binding.etEvidence.setText(mission.evidenceSubmitted)
            binding.etEvidence.isEnabled = false
        } else {
            binding.btnSubmitEvidence.isEnabled = true
            binding.btnSubmitEvidence.text = getString(R.string.btn_submit_evidence)
            binding.etEvidence.isEnabled = true
        }

        // Single-claim reward lock
        if (mission.isClaimed) {
            binding.btnClaimReward.isEnabled = false
            binding.btnClaimReward.text = "Đã nhận thưởng (Đã khóa)"
            binding.tvClaimStatus.text = "Bạn đã nhận phần thưởng cho nhiệm vụ này rồi."
        } else if (mission.assignmentStatus == MissionAssignmentStatus.Completed) {
            binding.btnClaimReward.isEnabled = true
            binding.btnClaimReward.text = getString(R.string.btn_claim_reward)
            binding.tvClaimStatus.text = "Nhiệm vụ đã được duyệt! Bấm Nhận thưởng ngay."
        } else {
            binding.btnClaimReward.isEnabled = false
            binding.btnClaimReward.text = "Chưa thể nhận thưởng"
            binding.tvClaimStatus.text = "Cần hoàn thành và được Ban tổ chức phê duyệt bằng chứng trước khi nhận thưởng."
        }
    }

    private fun startCountdown(expiresAt: Long) {
        countDownTimer?.cancel()
        val remaining = expiresAt - System.currentTimeMillis()
        if (remaining <= 0) {
            binding.tvCountdown.text = "Thời gian: Đã hết hạn!"
            return
        }

        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountdown.text = "Thời gian còn lại: ${DateTimeUtils.formatCountdown(millisUntilFinished)}"
            }
            override fun onFinish() {
                binding.tvCountdown.text = "Thời gian: Đã hết hạn!"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
