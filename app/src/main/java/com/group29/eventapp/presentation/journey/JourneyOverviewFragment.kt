package com.group29.eventapp.presentation.journey

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.UserJourneyStatus
import com.group29.eventapp.databinding.BottomSheetJourneyAlternativeBinding
import com.group29.eventapp.databinding.FragmentJourneyOverviewBinding
import com.group29.eventapp.presentation.journey.adapter.JourneyStepAdapter
import kotlinx.coroutines.launch

class JourneyOverviewFragment : Fragment() {

    private var _binding: FragmentJourneyOverviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JourneyOverviewViewModel by viewModels()
    private lateinit var stepAdapter: JourneyStepAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJourneyOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "event_2"

        setupToolbar()
        setupRecyclerView()
        setupActions()
        observeViewModel()

        viewModel.loadJourney(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadJourney(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        stepAdapter = JourneyStepAdapter { step ->
            val bundle = Bundle().apply {
                putString("stepId", step.id)
                putString("eventId", step.eventId)
            }
            findNavController().navigate(R.id.journeyStepDetailFragment, bundle)
        }

        binding.rvJourneySteps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stepAdapter
        }
    }

    private fun setupActions() {
        binding.btnSimulateScheduleChange.setOnClickListener {
            viewModel.simulateScheduleChange()
            Toast.makeText(requireContext(), "Mô phỏng: Ban tổ chức đã đổi giờ phiên!", Toast.LENGTH_SHORT).show()
        }

        binding.btnViewAlternative.setOnClickListener {
            showAlternativeBottomSheet()
        }
    }

    private fun showAlternativeBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetJourneyAlternativeBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnDismissAlternative.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAcceptAlternative.setOnClickListener {
            Toast.makeText(requireContext(), "Đã áp dụng phương án thay thế thành công!", Toast.LENGTH_SHORT).show()
            binding.cardNeedsUpdate.visibility = View.GONE
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.journeyState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvJourneySteps.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvJourneySteps.visibility = View.VISIBLE

                            val journey = state.data
                            binding.tvGoalTitle.text = "Mục tiêu: ${journey.goal}"
                            binding.tvProgressPercent.text = "${journey.progressPercent}%"
                            binding.progressJourney.progress = journey.progressPercent
                            binding.tvStatusLabel.text = "Trạng thái: ${journey.status.displayName}"

                            // Check conflict in steps (js_3 and js_4 overlap)
                            val hasConflict = journey.steps.any { s1 ->
                                journey.steps.any { s2 ->
                                    s1.id != s2.id && s1.isMandatory && s2.isMandatory &&
                                    maxOf(s1.startTime, s2.startTime) < minOf(s1.endTime, s2.endTime)
                                }
                            }
                            binding.cardConflictWarning.visibility = if (hasConflict) View.VISIBLE else View.GONE

                            // Check NeedsUpdate status
                            binding.cardNeedsUpdate.visibility = if (journey.status == UserJourneyStatus.NeedsUpdate) View.VISIBLE else View.GONE

                            stepAdapter.submitList(journey.steps)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvJourneySteps.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvJourneySteps.visibility = View.GONE
                            binding.layoutError.tvErrorMessage.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
