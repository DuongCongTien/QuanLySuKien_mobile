package com.group29.eventapp.presentation.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentOrganizerDashboardBinding
import com.group29.eventapp.presentation.organizer.adapter.OrganizerEventAdapter
import kotlinx.coroutines.launch
import java.util.Locale

class OrganizerDashboardFragment : Fragment() {

    private var _binding: FragmentOrganizerDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrganizerDashboardViewModel by viewModels()
    private lateinit var adapter: OrganizerEventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupListeners()
        observeData()
    }

    private fun setupAdapter() {
        adapter = OrganizerEventAdapter(
            events = emptyList(),
            onViewAttendees = { event ->
                val bundle = bundleOf("eventId" to event.id)
                findNavController().navigate(R.id.organizerAttendeesFragment, bundle)
            },
            onEditEvent = { event ->
                val bundle = bundleOf("eventId" to event.id)
                findNavController().navigate(R.id.organizerEventEditorFragment, bundle)
            }
        )
        binding.rvOrgEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrgEvents.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabCreateEvent.setOnClickListener {
            // Mở màn 11 để tạo sự kiện mới (không truyền eventId)
            findNavController().navigate(R.id.organizerEventEditorFragment)
        }

        binding.includeError.btnRetry.setOnClickListener {
            viewModel.loadDashboard()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.VISIBLE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE

                            val data = state.data
                            with(binding) {
                                tvTotalRegistered.text = data.totalRegistered.toString()
                                tvTotalCheckedIn.text = data.totalCheckedIn.toString()
                                tvTotalPending.text = data.totalPending.toString()
                                tvTotalCancelled.text = data.totalCancelled.toString()

                                tvAttendanceRatePercent.text = String.format(Locale.getDefault(), "%.1f%%", data.attendanceRate)
                                progressBarAttendanceRate.progress = data.attendanceRate.toInt()

                                adapter.submitList(data.managedEvents)
                            }
                        }
                        is UiState.Empty -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.VISIBLE
                            binding.includeError.tvErrorMessage.text = state.message
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
