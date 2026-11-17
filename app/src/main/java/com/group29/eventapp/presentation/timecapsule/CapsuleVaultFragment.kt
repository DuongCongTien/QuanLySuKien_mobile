package com.group29.eventapp.presentation.timecapsule

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
import com.google.android.material.tabs.TabLayout
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentCapsuleVaultBinding
import com.group29.eventapp.presentation.timecapsule.adapter.CapsuleAdapter
import kotlinx.coroutines.launch

class CapsuleVaultFragment : Fragment() {

    private var _binding: FragmentCapsuleVaultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CapsuleVaultViewModel by viewModels()
    private lateinit var capsuleAdapter: CapsuleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCapsuleVaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupTabs()
        setupRecyclerView()
        setupActions()
        observeViewModel()

        viewModel.loadVault()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadVault()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTabs() {
        binding.tabLayoutVault.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setTabIndex(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        capsuleAdapter = CapsuleAdapter { capsule ->
            val bundle = Bundle().apply {
                putString("capsuleId", capsule.id)
            }
            findNavController().navigate(R.id.capsuleDetailFragment, bundle)
        }

        binding.rvCapsules.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = capsuleAdapter
        }
    }

    private fun setupActions() {
        binding.fabCreateCapsule.setOnClickListener {
            findNavController().navigate(R.id.capsuleCreateFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.capsulesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.layoutLoading.root.visibility = View.VISIBLE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvCapsules.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvCapsules.visibility = View.VISIBLE
                            capsuleAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.VISIBLE
                            binding.layoutError.root.visibility = View.GONE
                            binding.rvCapsules.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmpty.root.visibility = View.GONE
                            binding.layoutError.root.visibility = View.VISIBLE
                            binding.rvCapsules.visibility = View.GONE
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
