package com.group29.eventapp.presentation.networking

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentSmartMatchBinding
import com.group29.eventapp.presentation.networking.adapter.InvitationAdapter
import com.group29.eventapp.presentation.networking.adapter.SmartMatchAdapter
import kotlinx.coroutines.launch

class SmartMatchFragment : Fragment() {

    private var _binding: FragmentSmartMatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SmartMatchViewModel by viewModels()
    private lateinit var matchAdapter: SmartMatchAdapter
    private lateinit var invitationAdapter: InvitationAdapter

    private var currentTabIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSmartMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupTabs()
        setupPrivacySwitch()
        observeViewModel()

        viewModel.loadData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupAdapters() {
        matchAdapter = SmartMatchAdapter { match ->
            viewModel.sendInvitation(match) { success ->
                if (success) {
                    Toast.makeText(requireContext(), getString(R.string.msg_invitation_sent, match.user.name), Toast.LENGTH_SHORT).show()
                }
            }
        }

        invitationAdapter = InvitationAdapter(
            onAcceptClick = { match ->
                viewModel.respondToInvitation(match, true) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), getString(R.string.msg_invitation_accepted, match.user.name), Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDeclineClick = { match ->
                viewModel.respondToInvitation(match, false) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), getString(R.string.msg_invitation_declined), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayoutMatch.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position ?: 0
                if (currentTabIndex == 0) {
                    binding.rvMatches.adapter = matchAdapter
                } else {
                    binding.rvMatches.adapter = invitationAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupPrivacySwitch() {
        binding.switchPrivacy.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPrivacyEnabled(isChecked)
            val msg = if (isChecked) "Hồ sơ của bạn hiện đang hiển thị trên Smart Match" else "Hồ sơ của bạn đã ẩn trên Smart Match"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.suggestedMatchesState.collect { state ->
                        if (currentTabIndex == 0) {
                            handleState(state) { matchAdapter.submitList(it) }
                        }
                    }
                }

                launch {
                    viewModel.invitationsState.collect { state ->
                        if (currentTabIndex == 1) {
                            handleState(state) { invitationAdapter.submitList(it) }
                        }
                    }
                }
            }
        }
    }

    private fun <T> handleState(state: UiState<List<T>>, onSuccess: (List<T>) -> Unit) {
        when (state) {
            is UiState.Loading -> {
                binding.layoutLoading.root.visibility = View.VISIBLE
                binding.layoutEmpty.root.visibility = View.GONE
                binding.layoutError.root.visibility = View.GONE
                binding.rvMatches.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutEmpty.root.visibility = View.GONE
                binding.layoutError.root.visibility = View.GONE
                binding.rvMatches.visibility = View.VISIBLE
                onSuccess(state.data)
            }
            is UiState.Empty -> {
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutEmpty.root.visibility = View.VISIBLE
                binding.layoutError.root.visibility = View.GONE
                binding.rvMatches.visibility = View.GONE
            }
            is UiState.Error -> {
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutEmpty.root.visibility = View.GONE
                binding.layoutError.root.visibility = View.VISIBLE
                binding.rvMatches.visibility = View.GONE
                binding.layoutError.tvErrorMessage.text = state.message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
