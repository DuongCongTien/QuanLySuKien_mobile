package com.group29.eventapp.presentation.waitlist

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentWaitlistJoinBinding
import kotlinx.coroutines.launch

class WaitlistJoinFragment : Fragment() {

    private var _binding: FragmentWaitlistJoinBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WaitlistViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWaitlistJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "event_4"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupActions()
        observeViewModel()

        viewModel.loadStatus(eventId)
    }

    private fun setupActions() {
        binding.btnLeaveWaitlist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Rời danh sách chờ?")
                .setMessage("Bạn sẽ mất vị trí hiện tại trong hàng đợi và cần đăng ký lại từ đầu nếu muốn tham gia lại.")
                .setPositiveButton("Rời hàng đợi") { _, _ ->
                    viewModel.leaveWaitlist { success, msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        if (success) {
                            findNavController().navigateUp()
                        }
                    }
                }
                .setNegativeButton(R.string.btn_cancel, null)
                .show()
        }

        binding.btnViewOffer.setOnClickListener {
            // Mở Màn 31: SeatOfferFragment
            findNavController().navigate(R.id.seatOfferFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.entryState.collect { state ->
                        if (state is UiState.Success) {
                            val entry = state.data
                            binding.cardQueueStatus.visibility = View.VISIBLE
                            binding.tvQueuePosition.text = "#${entry.queuePosition}"
                            binding.tvJoinedTime.text = getString(R.string.waitlist_joined_time, DateTimeUtils.formatDateTime(entry.joinedAt))
                        } else {
                            binding.cardQueueStatus.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.activeOffer.collect { offer ->
                        binding.cardOfferAlert.visibility = if (offer != null) View.VISIBLE else View.GONE
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
