package com.group29.eventapp.presentation.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.databinding.FragmentTicketBinding
import kotlinx.coroutines.launch

class TicketFragment : Fragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by viewModels()

    private var eventId: String? = null
    private var ticketCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString("eventId")
        ticketCode = arguments?.getString("ticketCode")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBackToEvent.setOnClickListener {
            if (eventId != null) {
                val bundle = bundleOf("eventId" to eventId)
                findNavController().navigate(R.id.eventDetailFragment, bundle)
            } else {
                findNavController().navigateUp()
            }
        }

        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.includeError.btnRetry.setOnClickListener {
            viewModel.loadTicket(eventId, ticketCode)
        }

        observeData()
        viewModel.loadTicket(eventId, ticketCode)
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
                            val event = state.data.event
                            val reg = state.data.registration
                            eventId = event.id

                            with(binding) {
                                tvTicketEventCategory.text = "${event.category} • Vé tham dự chính thức"
                                tvTicketEventTitle.text = event.title
                                tvTicketTime.text = "${DateTimeUtils.formatDateTime(event.startTime)} - ${DateTimeUtils.formatDateTime(event.endTime)}"
                                tvTicketLocation.text = if (event.isOnline) "Trực tuyến" else event.location
                                tvTicketAttendeeName.text = reg.userName
                                tvTicketCodeNumber.text = reg.ticketCode

                                if (reg.checkInStatus == CheckInStatus.CheckedIn) {
                                    val checkInTimeStr = reg.checkInAt?.let { " (${DateTimeUtils.formatTimeOnly(it)})" } ?: ""
                                    tvTicketCheckInStatus.text = "Đã check-in$checkInTimeStr"
                                    tvTicketCheckInStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                                    tvTicketCheckInStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success))
                                } else {
                                    tvTicketCheckInStatus.text = "Chưa check-in"
                                    tvTicketCheckInStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                                    tvTicketCheckInStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_info))
                                }
                            }
                        }
                        is UiState.Empty -> {
                            binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                            binding.includeError.layoutErrorRoot.visibility = View.GONE
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
}
