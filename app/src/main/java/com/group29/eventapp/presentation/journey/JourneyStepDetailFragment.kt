package com.group29.eventapp.presentation.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.JourneyStepModel
import com.group29.eventapp.data.model.JourneyStepProgress
import com.group29.eventapp.databinding.DialogSkipStepBinding
import com.group29.eventapp.databinding.FragmentJourneyStepDetailBinding
import kotlinx.coroutines.launch

class JourneyStepDetailFragment : Fragment() {

    private var _binding: FragmentJourneyStepDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JourneyStepDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJourneyStepDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stepId = arguments?.getString("stepId") ?: "js_1"
        val eventId = arguments?.getString("eventId") ?: "event_2"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupActions()
        observeViewModel()

        viewModel.loadStep(stepId, eventId)
    }

    private fun setupActions() {
        binding.btnSimulateCheckIn.setOnClickListener {
            viewModel.checkIn { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCompleteStep.setOnClickListener {
            viewModel.completeStep { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSkipStep.setOnClickListener {
            showSkipDialog()
        }
    }

    private fun showSkipDialog() {
        val dialogBinding = DialogSkipStepBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancelSkip.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnConfirmSkip.setOnClickListener {
            val reason = dialogBinding.etSkipReason.text.toString().trim()
            if (reason.isEmpty()) {
                dialogBinding.tilSkipReason.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }

            viewModel.skipStep(reason) { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stepState.collect { state ->
                    if (state is UiState.Success) {
                        bindStep(state.data)
                    }
                }
            }
        }
    }

    private fun bindStep(step: JourneyStepModel) {
        val context = requireContext()
        binding.tvStepTitle.text = step.title
        val startStr = DateTimeUtils.formatTimeOnly(step.startTime)
        val endStr = DateTimeUtils.formatTimeOnly(step.endTime)
        binding.tvStepTime.text = "Thời gian: $startStr - $endStr (${step.durationMinutes} phút)"
        binding.tvStepLocation.text = "Địa điểm: ${step.location}"
        binding.tvMapLocation.text = step.location
        binding.tvStepReason.text = "Lý do đề xuất: ${step.recommendationReason}"

        if (step.isMandatory) {
            binding.chipMandatory.text = getString(R.string.journey_step_mandatory)
            binding.chipMandatory.setChipBackgroundColorResource(R.color.color_warning_container)
            binding.chipMandatory.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
            binding.btnSkipStep.visibility = View.GONE
        } else {
            binding.chipMandatory.text = getString(R.string.journey_step_optional)
            binding.chipMandatory.setChipBackgroundColorResource(R.color.color_surface_variant)
            binding.chipMandatory.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
            binding.btnSkipStep.visibility = if (step.progress == JourneyStepProgress.Completed || step.progress == JourneyStepProgress.Skipped) View.GONE else View.VISIBLE
        }

        binding.chipStatus.text = step.progress.displayName

        binding.tvCheckInRequirement.text = if (step.requiresCheckIn) {
            "• Yêu cầu check-in có mặt: Có (Bắt buộc check-in)"
        } else {
            "• Yêu cầu check-in có mặt: Không bắt buộc"
        }

        if (step.isCheckedIn) {
            binding.tvCheckInState.text = "• Trạng thái check-in: Đã check-in có mặt"
            binding.tvCheckInState.setTextColor(ContextCompat.getColor(context, R.color.status_success))
            binding.btnSimulateCheckIn.isEnabled = false
            binding.btnSimulateCheckIn.text = "Đã check-in"
        } else {
            binding.tvCheckInState.text = "• Trạng thái check-in: Chưa check-in"
            binding.tvCheckInState.setTextColor(ContextCompat.getColor(context, R.color.status_warning))
            binding.btnSimulateCheckIn.isEnabled = true
            binding.btnSimulateCheckIn.text = getString(R.string.btn_simulate_checkin)
        }

        // Complete button condition
        if (step.progress == JourneyStepProgress.Completed) {
            binding.btnCompleteStep.isEnabled = false
            binding.btnCompleteStep.text = "Đã hoàn thành bước này"
        } else if (step.progress == JourneyStepProgress.Skipped) {
            binding.btnCompleteStep.isEnabled = false
            binding.btnCompleteStep.text = "Đã bỏ qua bước này"
        } else {
            // Locked if requires check in and not checked in
            val canComplete = !step.requiresCheckIn || step.isCheckedIn
            binding.btnCompleteStep.isEnabled = canComplete
            binding.btnCompleteStep.text = if (canComplete) getString(R.string.btn_mark_completed) else "Cần check-in để hoàn thành"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
