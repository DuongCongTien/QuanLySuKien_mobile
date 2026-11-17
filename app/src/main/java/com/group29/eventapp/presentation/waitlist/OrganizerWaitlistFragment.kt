package com.group29.eventapp.presentation.waitlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentOrganizerWaitlistBinding
import com.group29.eventapp.presentation.waitlist.adapter.OrganizerWaitlistAdapter
import kotlinx.coroutines.launch

class OrganizerWaitlistFragment : Fragment() {

    private var _binding: FragmentOrganizerWaitlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrganizerWaitlistViewModel by viewModels()
    private lateinit var waitlistAdapter: OrganizerWaitlistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerWaitlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "event_4"

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadWaitlist(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadWaitlist(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        waitlistAdapter = OrganizerWaitlistAdapter()
        binding.rvWaitlist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = waitlistAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.entriesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvWaitlist.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvWaitlist.visibility = View.VISIBLE

                            binding.tvCountConfirmed.text = viewModel.countConfirmed.toString()
                            binding.tvCountHeld.text = viewModel.countHeld.toString()
                            binding.tvCountWaiting.text = viewModel.countWaiting.toString()

                            waitlistAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvWaitlist.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvWaitlist.visibility = View.GONE
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
