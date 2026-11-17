package com.group29.eventapp.presentation.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentOrganizerAttendeesBinding
import com.group29.eventapp.presentation.organizer.adapter.OrganizerAttendeeAdapter
import kotlinx.coroutines.launch

class OrganizerAttendeesFragment : Fragment() {

    private var _binding: FragmentOrganizerAttendeesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrganizerAttendeesViewModel by viewModels()
    private lateinit var attendeeAdapter: OrganizerAttendeeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerAttendeesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "evt_01"

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupSearch()
        observeViewModel()

        viewModel.loadAttendees(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAttendees(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        attendeeAdapter = OrganizerAttendeeAdapter { attendee ->
            viewModel.manualCheckIn(attendee) { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvAttendees.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attendeeAdapter
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipFilterCheckedIn) -> viewModel.onFilterChanged(AttendeeFilter.CHECKED_IN)
                checkedIds.contains(R.id.chipFilterNotCheckedIn) -> viewModel.onFilterChanged(AttendeeFilter.NOT_CHECKED_IN)
                checkedIds.contains(R.id.chipFilterCancelled) -> viewModel.onFilterChanged(AttendeeFilter.CANCELLED)
                else -> viewModel.onFilterChanged(AttendeeFilter.ALL)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.onSearchQueryChanged(text?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.attendeesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvAttendees.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvAttendees.visibility = View.VISIBLE
                            attendeeAdapter.submitList(state.data)
                            binding.tvTotalCount.text = getString(R.string.attendee_total_count, state.data.size)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvAttendees.visibility = View.GONE
                            binding.tvTotalCount.text = getString(R.string.attendee_total_count, 0)
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvAttendees.visibility = View.GONE
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
