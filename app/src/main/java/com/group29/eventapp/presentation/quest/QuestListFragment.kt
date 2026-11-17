package com.group29.eventapp.presentation.quest

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
import com.google.android.material.tabs.TabLayout
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentQuestListBinding
import com.group29.eventapp.presentation.quest.adapter.LeaderboardAdapter
import com.group29.eventapp.presentation.quest.adapter.QuestAdapter
import kotlinx.coroutines.launch

class QuestListFragment : Fragment() {

    private var _binding: FragmentQuestListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuestViewModel by viewModels()
    private lateinit var questAdapter: QuestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuestListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "evt_01"

        setupToolbar()
        setupTabs()
        setupRecyclerViews()
        setupLeaderboardToggle()
        observeViewModel()

        viewModel.loadQuests(eventId)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadQuests(eventId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTabs() {
        binding.tabLayoutQuests.apply {
            addTab(newTab().setText(R.string.tab_quest_all))
            addTab(newTab().setText(R.string.tab_quest_in_progress))
            addTab(newTab().setText(R.string.tab_quest_pending))
            addTab(newTab().setText(R.string.tab_quest_completed))
            addTab(newTab().setText(R.string.tab_quest_expired))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.setFilterIndex(tab?.position ?: 0)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun setupRecyclerViews() {
        questAdapter = QuestAdapter { quest ->
            Toast.makeText(requireContext(), "Đã gửi bằng chứng cho nhiệm vụ: ${quest.title}!", Toast.LENGTH_SHORT).show()
        }
        binding.rvQuests.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questAdapter
        }

        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupLeaderboardToggle() {
        binding.switchLeaderboard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.swipeRefresh.visibility = View.GONE
                binding.rvLeaderboard.visibility = View.VISIBLE
            } else {
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.rvLeaderboard.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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

                                updateStats()
                            }
                            is UiState.Empty -> {
                                binding.layoutLoading.root.visibility = View.GONE
                                binding.layoutEmpty.root.visibility = View.VISIBLE
                                binding.layoutError.root.visibility = View.GONE
                                binding.rvQuests.visibility = View.GONE
                                updateStats()
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

                launch {
                    viewModel.leaderboardUsers.collect { users ->
                        binding.rvLeaderboard.adapter = LeaderboardAdapter(users)
                    }
                }
            }
        }
    }

    private fun updateStats() {
        binding.tvTotalPoints.text = "${viewModel.totalPoints} EXP"
        binding.tvProgressRatio.text = "Đã hoàn thành: ${viewModel.completedCount}/${viewModel.totalCount} nhiệm vụ"
        val progress = if (viewModel.totalCount > 0) {
            (viewModel.completedCount * 100) / viewModel.totalCount
        } else 0
        binding.progressQuests.progress = progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
