package com.group29.eventapp.presentation.events

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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentMyEventsBinding
import com.group29.eventapp.databinding.FragmentMyEventsPageBinding
import com.group29.eventapp.presentation.events.adapter.MyEventItem
import com.group29.eventapp.presentation.events.adapter.MyEventItemAdapter
import kotlinx.coroutines.launch

class MyEventsFragment : Fragment() {

    private var _binding: FragmentMyEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyEventsViewModel by viewModels()
    private lateinit var pagerAdapter: MyEventsPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = MyEventsPagerAdapter(
            onViewTicket = { item ->
                val bundle = bundleOf(
                    "eventId" to item.event.id,
                    "ticketCode" to item.registration.ticketCode
                )
                findNavController().navigate(R.id.ticketFragment, bundle)
            },
            onViewEvent = { item ->
                val bundle = bundleOf("eventId" to item.event.id)
                findNavController().navigate(R.id.eventDetailFragment, bundle)
            },
            onRefresh = {
                viewModel.loadMyEvents()
            }
        )

        binding.viewPagerMyEvents.adapter = pagerAdapter

        val tabTitles = listOf("Sắp tham gia", "Đã tham gia", "Đã hủy")
        TabLayoutMediator(binding.tabLayoutMyEvents, binding.viewPagerMyEvents) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.includeError.btnRetry.setOnClickListener {
            viewModel.loadMyEvents()
        }

        observeData()
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

                            pagerAdapter.updateData(
                                upcoming = state.data.upcoming,
                                attended = state.data.attended,
                                cancelled = state.data.cancelled
                            )
                        }
                        is UiState.Empty -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                            pagerAdapter.updateData(emptyList(), emptyList(), emptyList())
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

    private class MyEventsPagerAdapter(
        private val onViewTicket: (MyEventItem) -> Unit,
        private val onViewEvent: (MyEventItem) -> Unit,
        private val onRefresh: () -> Unit
    ) : RecyclerView.Adapter<MyEventsPagerAdapter.PageViewHolder>() {

        private var upcomingList = listOf<MyEventItem>()
        private var attendedList = listOf<MyEventItem>()
        private var cancelledList = listOf<MyEventItem>()

        fun updateData(upcoming: List<MyEventItem>, attended: List<MyEventItem>, cancelled: List<MyEventItem>) {
            upcomingList = upcoming
            attendedList = attended
            cancelledList = cancelled
            notifyDataSetChanged()
        }

        class PageViewHolder(val binding: FragmentMyEventsPageBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val binding = FragmentMyEventsPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PageViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val items = when (position) {
                0 -> upcomingList
                1 -> attendedList
                else -> cancelledList
            }

            val emptyMessage = when (position) {
                0 -> "Bạn chưa có sự kiện nào sắp tham gia.\nHãy khám phá các sự kiện mới ngay!"
                1 -> "Bạn chưa tham dự sự kiện nào."
                else -> "Không có vé nào bị hủy."
            }

            with(holder.binding) {
                swipeRefreshPage.setOnRefreshListener {
                    swipeRefreshPage.isRefreshing = false
                    onRefresh()
                }

                if (items.isEmpty()) {
                    includeEmpty.layoutEmptyRoot.visibility = View.VISIBLE
                    includeEmpty.tvEmptyMessage.text = emptyMessage
                    rvPageItems.visibility = View.GONE
                } else {
                    includeEmpty.layoutEmptyRoot.visibility = View.GONE
                    rvPageItems.visibility = View.VISIBLE
                    rvPageItems.layoutManager = LinearLayoutManager(root.context)
                    val adapter = MyEventItemAdapter(items, onViewTicket, onViewEvent)
                    rvPageItems.adapter = adapter
                }
            }
        }

        override fun getItemCount(): Int = 3
    }
}
