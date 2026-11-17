package com.group29.eventapp.presentation.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.group29.eventapp.databinding.DialogAddQuestBinding
import com.group29.eventapp.databinding.FragmentOrganizerQuestEditorBinding
import com.group29.eventapp.presentation.organizer.adapter.OrganizerQuestAdapter
import kotlinx.coroutines.launch

class OrganizerQuestEditorFragment : Fragment() {

    private var _binding: FragmentOrganizerQuestEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrganizerQuestEditorViewModel by viewModels()
    private lateinit var questAdapter: OrganizerQuestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerQuestEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "evt_01"

        setupRecyclerView()
        setupActions()
        observeViewModel()

        viewModel.loadQuests(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadQuests(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        questAdapter = OrganizerQuestAdapter { quest ->
            Toast.makeText(requireContext(), "Nhiệm vụ: ${quest.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvQuests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questAdapter
        }
    }

    private fun setupActions() {
        binding.btnNavigateReviewMissions.setOnClickListener {
            // Mở màn 26: Duyệt bằng chứng nhiệm vụ bí mật
            findNavController().navigate(R.id.organizerMissionReviewFragment)
        }

        binding.fabAddQuest.setOnClickListener {
            showAddQuestDialog()
        }
    }

    private fun showAddQuestDialog() {
        val dialogBinding = DialogAddQuestBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSaveQuest.setOnClickListener {
            val title = dialogBinding.etQuestTitle.text.toString().trim()
            val desc = dialogBinding.etQuestDesc.text.toString().trim()
            val pointsStr = dialogBinding.etQuestPoints.text.toString().trim()
            val maxAttemptsStr = dialogBinding.etQuestMaxAttempts.text.toString().trim()
            val verification = dialogBinding.etQuestVerification.text.toString().trim()

            if (title.isEmpty()) {
                dialogBinding.tilQuestTitle.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                dialogBinding.tilQuestDesc.error = getString(R.string.val_field_required)
                return@setOnClickListener
            }
            val points = pointsStr.toIntOrNull() ?: 50
            val maxAttempts = maxAttemptsStr.toIntOrNull() ?: 1

            viewModel.createQuest(title, desc, points, maxAttempts, verification) { success, msg ->
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
                viewModel.questsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvQuests.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvQuests.visibility = View.VISIBLE
                            questAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvQuests.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvQuests.visibility = View.GONE
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
