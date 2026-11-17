package com.group29.eventapp.presentation.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
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
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.databinding.FragmentSearchFilterBinding
import com.group29.eventapp.presentation.events.adapter.EventListAdapter
import kotlinx.coroutines.launch

class SearchFilterFragment : Fragment() {

    private var _binding: FragmentSearchFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchFilterViewModel by viewModels()
    private lateinit var adapter: EventListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupAdapter()
        observeData()
    }

    private fun setupUI() {
        // Nhập từ khóa
        binding.etSearchQuery.doAfterTextChanged { text ->
            viewModel.updateQuery(text?.toString().orEmpty())
        }

        binding.etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.performSearch()
                true
            } else false
        }

        // Mở BottomSheet lọc
        binding.btnOpenFilter.setOnClickListener {
            val bottomSheet = FilterBottomSheetFragment(
                currentCategory = viewModel.currentCategory,
                currentStatus = viewModel.currentStatus,
                currentIsOnline = viewModel.currentIsOnline
            ) { category, status, isOnline ->
                viewModel.applyFilters(category, status, isOnline)
                updateFilterInfoLabel()
            }
            bottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }

        // Nút đặt lại trong Empty State
        binding.includeEmpty.btnEmptyAction.visibility = View.VISIBLE
        binding.includeEmpty.btnEmptyAction.text = "Đặt lại bộ lọc"
        binding.includeEmpty.btnEmptyAction.setOnClickListener {
            binding.etSearchQuery.setText("")
            viewModel.resetFilters()
            updateFilterInfoLabel()
        }

        // Retry trong Error
        binding.includeError.btnRetry.setOnClickListener {
            viewModel.performSearch()
        }
    }

    private fun setupAdapter() {
        adapter = EventListAdapter { event ->
            val bundle = bundleOf("eventId" to event.id)
            findNavController().navigate(R.id.eventDetailFragment, bundle)
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = adapter
    }

    private fun updateFilterInfoLabel() {
        val conditions = mutableListOf<String>()
        if (!viewModel.currentQuery.isBlank()) conditions.add("Từ khóa: \"${viewModel.currentQuery}\"")
        if (viewModel.currentCategory != null) conditions.add("Chủ đề: ${viewModel.currentCategory}")
        if (viewModel.currentIsOnline != null) conditions.add(if (viewModel.currentIsOnline == true) "Trực tuyến" else "Trực tiếp")
        if (viewModel.currentStatus != null) conditions.add(viewModel.currentStatus!!.displayName)

        binding.tvActiveFiltersInfo.text = if (conditions.isEmpty()) {
            "Hiển thị tất cả sự kiện"
        } else {
            "Đang lọc theo: ${conditions.joinToString(" • ")}"
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.VISIBLE
                            binding.includeEmpty.layoutEmptyRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeEmpty.layoutEmptyRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                            adapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeEmpty.layoutEmptyRoot.visibility = View.VISIBLE
                            binding.includeEmpty.tvEmptyMessage.text = state.message
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
                            adapter.submitList(emptyList())
                        }
                        is UiState.Error -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeEmpty.layoutEmptyRoot.visibility = View.GONE
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
