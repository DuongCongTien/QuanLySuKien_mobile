package com.group29.eventapp.presentation.secretmission

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.databinding.DialogRejectMissionBinding
import com.group29.eventapp.databinding.FragmentOrganizerMissionReviewBinding
import com.group29.eventapp.presentation.secretmission.adapter.OrganizerMissionReviewAdapter
import kotlinx.coroutines.launch

class OrganizerMissionReviewFragment : Fragment() {

    private var _binding: FragmentOrganizerMissionReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrganizerMissionReviewViewModel by viewModels()
    private lateinit var reviewAdapter: OrganizerMissionReviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerMissionReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "event_2"

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadQueue(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadQueue(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        reviewAdapter = OrganizerMissionReviewAdapter(
            onApproveClick = { mission ->
                viewModel.approveMission(mission.id) { success, msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            },
            onRejectClick = { mission ->
                showRejectDialog(mission)
            }
        )

        binding.rvReviewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }
    }

    private fun showRejectDialog(mission: SecretMissionModel) {
        val dialogBinding = DialogRejectMissionBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancelReject.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnConfirmReject.setOnClickListener {
            val reason = dialogBinding.etRejectionReason.text.toString().trim()
            if (reason.isEmpty()) {
                dialogBinding.tilRejectionReason.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }

            viewModel.rejectMission(mission.id, reason) { success, msg ->
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
                viewModel.reviewQueueState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvReviewList.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvReviewList.visibility = View.VISIBLE
                            reviewAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvReviewList.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvReviewList.visibility = View.GONE
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
