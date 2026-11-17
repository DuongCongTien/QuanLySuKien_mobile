package com.group29.eventapp.presentation.home

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
import com.google.android.material.chip.Chip
import com.group29.eventapp.R
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.databinding.FragmentHomeBinding
import com.group29.eventapp.presentation.events.adapter.EventListAdapter
import com.group29.eventapp.presentation.events.adapter.FeaturedEventAdapter
import com.group29.eventapp.presentation.events.adapter.RecommendedEventAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var featuredAdapter: FeaturedEventAdapter
    private lateinit var recommendedAdapter: RecommendedEventAdapter
    private lateinit var upcomingAdapter: EventListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupAdapters()
        observeData()
    }

    private fun setupUI() {
        // Tên người dùng và nút thông báo
        binding.tvGreeting.text = "Xin chào, ${SessionManager.currentUserName.value} 👋"

        binding.btnNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }

        // Bấm vào search bar mở màn Tìm kiếm & Bộ lọc
        binding.cardSearchTrigger.setOnClickListener {
            findNavController().navigate(R.id.searchFilterFragment)
        }

        // Swipe refresh
        binding.swipeRefreshHome.setOnRefreshListener {
            viewModel.refresh()
        }

        // Error retry
        binding.includeError.btnRetry.setOnClickListener {
            viewModel.refresh()
        }

        // Chip group filter danh mục
        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds.first())
                val categoryName = chip.text.toString()
                viewModel.setCategory(categoryName)
            }
        }
    }

    private fun setupAdapters() {
        // Featured Carousel
        featuredAdapter = FeaturedEventAdapter(emptyList()) { event ->
            navigateToEventDetail(event)
        }
        binding.viewPagerFeatured.adapter = featuredAdapter

        // Recommended (Horizontal)
        recommendedAdapter = RecommendedEventAdapter(emptyList()) { event ->
            navigateToEventDetail(event)
        }
        binding.rvRecommendedEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendedEvents.adapter = recommendedAdapter

        // Upcoming (Vertical)
        upcomingAdapter = EventListAdapter { event ->
            navigateToEventDetail(event)
        }
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.adapter = upcomingAdapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefreshHome.isRefreshing = false

                    when (state) {
                        is UiState.Loading -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.VISIBLE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE

                            featuredAdapter.submitList(state.data.featuredEvents)
                            recommendedAdapter.submitList(state.data.recommendedEvents)
                            upcomingAdapter.submitList(state.data.upcomingEvents)
                        }
                        is UiState.Empty -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                            upcomingAdapter.submitList(emptyList())
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

    private fun navigateToEventDetail(event: EventModel) {
        val bundle = bundleOf("eventId" to event.id)
        findNavController().navigate(R.id.eventDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
