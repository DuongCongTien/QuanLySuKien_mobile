package com.group29.eventapp.presentation.notification

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
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.NotificationType
import com.group29.eventapp.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadNotifications()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMarkAllRead.setOnClickListener {
            viewModel.markAllAsRead()
            Toast.makeText(requireContext(), getString(R.string.notif_mark_all_read), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { notif ->
            viewModel.markAsRead(notif.id)
            when (notif.type) {
                NotificationType.Mission -> findNavController().navigate(R.id.secretMissionInboxFragment)
                NotificationType.TimeCapsule -> findNavController().navigate(R.id.capsuleVaultFragment)
                NotificationType.SeatOffer -> findNavController().navigate(R.id.seatOfferFragment)
                NotificationType.Networking -> findNavController().navigate(R.id.smartMatchFragment)
                else -> Toast.makeText(requireContext(), notif.title, Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
    }

    private fun setupFilters() {
        binding.chipGroupNotif.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipFilterEvent) -> viewModel.setFilter(NotificationType.EventUpdate)
                checkedIds.contains(R.id.chipFilterMission) -> viewModel.setFilter(NotificationType.Mission)
                checkedIds.contains(R.id.chipFilterCapsule) -> viewModel.setFilter(NotificationType.TimeCapsule)
                checkedIds.contains(R.id.chipFilterOffer) -> viewModel.setFilter(NotificationType.SeatOffer)
                else -> viewModel.setFilter(null)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvNotifications.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvNotifications.visibility = View.VISIBLE
                            notificationAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvNotifications.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvNotifications.visibility = View.GONE
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
