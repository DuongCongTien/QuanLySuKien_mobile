package com.group29.eventapp.presentation.events

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.data.model.RegistrationModel
import com.group29.eventapp.databinding.FragmentEventDetailBinding
import com.group29.eventapp.presentation.events.adapter.EventSessionAdapter
import kotlinx.coroutines.launch

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()
    private var eventId: String = "event_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString("eventId") ?: "event_1"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.includeError.btnRetry.setOnClickListener {
            viewModel.loadEvent(eventId)
        }

        observeData()
        viewModel.loadEvent(eventId)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.includeLoading.layoutLoadingRoot.visibility = View.VISIBLE
                                binding.includeError.layoutErrorRoot.visibility = View.GONE
                            }
                            is UiState.Success -> {
                                binding.includeLoading.layoutLoadingRoot.visibility = View.GONE
                                binding.includeError.layoutErrorRoot.visibility = View.GONE
                                bindEventDetail(state.data.event, state.data.userRegistration)
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

                launch {
                    viewModel.actionMessage.collect { msg ->
                        if (msg != null) {
                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                            viewModel.clearActionMessage()
                        }
                    }
                }
            }
        }
    }

    private fun bindEventDetail(event: EventModel, registration: RegistrationModel?) {
        with(binding) {
            tvDetailCategory.text = event.category
            tvDetailStatus.text = event.status.displayName
            tvDetailTitle.text = event.title
            tvDetailOrganizer.text = "Tổ chức bởi: ${event.organizerName}"

            tvDetailTime.text = "Thời gian: ${DateTimeUtils.formatDateTime(event.startTime)} - ${DateTimeUtils.formatDateTime(event.endTime)}"
            tvDetailLocation.text = "Địa điểm: ${if (event.isOnline) "Trực tuyến" else event.location}"

            // Sức chứa & Progress bar
            val capacityPercent = if (event.capacity > 0) (event.registeredCount * 100) / event.capacity else 0
            progressBarCapacity.progress = capacityPercent
            tvDetailCapacityPercent.text = "$capacityPercent%"
            tvDetailCapacityLabel.text = "Số lượng đăng ký: ${event.registeredCount} / ${event.capacity} chỗ"

            // Status chip color
            when (event.status) {
                EventStatus.Published -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success))
                }
                EventStatus.Ongoing -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_info))
                }
                EventStatus.RegistrationClosed -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_warning)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_warning))
                }
                EventStatus.Completed -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_neutral))
                }
                EventStatus.Cancelled -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_error)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error))
                }
                EventStatus.Draft -> {
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_locked))
                }
            }

            // Description, speakers, requirements
            tvDetailDescription.text = event.description
            tvDetailSpeakers.text = event.speakerNames.joinToString(", ")
            tvDetailRequirements.text = event.requirements

            // Sessions
            rvDetailSessions.layoutManager = LinearLayoutManager(requireContext())
            rvDetailSessions.adapter = EventSessionAdapter(event.sessions)

            // Setup Event Utilities
            setupUtilities(event, registration)

            // Setup Dynamic Bottom Action Buttons
            setupBottomActions(event, registration)
        }
    }

    private fun setupUtilities(event: EventModel, registration: RegistrationModel?) {
        val bundle = bundleOf("eventId" to event.id)

        // 1. Hành trình (Journey)
        with(binding.utilJourney) {
            ivUtilityIcon.setImageResource(R.drawable.ic_nav_events)
            tvUtilityTitle.text = "Lịch trình (Journey)"
            tvUtilitySubtitle.text = "Cá nhân hóa AI"
            root.setOnClickListener {
                findNavController().navigate(R.id.journeyOverviewFragment, bundle)
            }
        }

        // 2. Nhiệm vụ (Quest)
        with(binding.utilQuest) {
            ivUtilityIcon.setImageResource(R.drawable.ic_nav_quests)
            tvUtilityTitle.text = "Nhiệm vụ (Quest)"
            tvUtilitySubtitle.text = "Tích điểm & thưởng"
            root.setOnClickListener {
                findNavController().navigate(R.id.questListFragment, bundle)
            }
        }

        // 3. Mật vụ bí mật (Secret Mission)
        with(binding.utilSecretMission) {
            ivUtilityIcon.setImageResource(R.drawable.ic_lock_key)
            tvUtilityTitle.text = "Secret Mission"
            tvUtilitySubtitle.text = "Thử thách ẩn danh"
            root.setOnClickListener {
                findNavController().navigate(R.id.secretMissionInboxFragment, bundle)
            }
        }

        // 4. Trợ lý AI (AI Assistant)
        with(binding.utilAi) {
            ivUtilityIcon.setImageResource(R.drawable.ic_send)
            tvUtilityTitle.text = "Trợ lý sự kiện AI"
            tvUtilitySubtitle.text = "Hỏi đáp thông minh"
            root.setOnClickListener {
                findNavController().navigate(R.id.aiAssistantFragment, bundle)
            }
        }

        // 5. Sơ đồ Live Map
        with(binding.utilMap) {
            ivUtilityIcon.setImageResource(R.drawable.ic_location)
            tvUtilityTitle.text = "Sơ đồ mặt bằng"
            tvUtilitySubtitle.text = "Bản đồ các phòng"
            root.setOnClickListener {
                findNavController().navigate(R.id.liveMapFragment, bundle)
            }
        }

        // 6. Kỷ niệm (Event Memory)
        with(binding.utilMemory) {
            ivUtilityIcon.setImageResource(R.drawable.ic_onboarding_3)
            tvUtilityTitle.text = "Kỷ niệm (Memory)"
            tvUtilitySubtitle.text = "Tổng kết & Kỷ niệm"
            val isAvailable = event.status == EventStatus.Completed || registration != null
            root.alpha = if (isAvailable) 1.0f else 0.4f
            root.setOnClickListener {
                if (isAvailable) {
                    findNavController().navigate(R.id.eventMemoryFragment, bundle)
                } else {
                    Toast.makeText(requireContext(), "Kỷ niệm sự kiện sẽ mở sau khi bạn tham dự!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 7. Hộp thời gian (Time Capsule)
        with(binding.utilCapsule) {
            ivUtilityIcon.setImageResource(R.drawable.ic_clock)
            tvUtilityTitle.text = "Hộp thời gian"
            tvUtilitySubtitle.text = "Gửi thông điệp số"
            root.setOnClickListener {
                findNavController().navigate(R.id.capsuleVaultFragment, bundle)
            }
        }

        // 8. Danh sách chờ (Waitlist)
        with(binding.utilWaitlist) {
            ivUtilityIcon.setImageResource(R.drawable.ic_nav_dashboard)
            tvUtilityTitle.text = "Danh sách chờ"
            tvUtilitySubtitle.text = "Hàng đợi thông minh"
            root.setOnClickListener {
                findNavController().navigate(R.id.waitlistJoinFragment, bundle)
            }
        }
    }

    private fun setupBottomActions(event: EventModel, registration: RegistrationModel?) {
        val isRegistered = registration != null
        val isCancelled = event.status == EventStatus.Cancelled
        val isFull = event.registeredCount >= event.capacity || event.status == EventStatus.RegistrationClosed

        binding.btnRegisterEvent.visibility = View.GONE
        binding.layoutRegisteredActions.visibility = View.GONE
        binding.layoutWaitlistActions.visibility = View.GONE
        binding.btnDisabledEvent.visibility = View.GONE

        when {
            // Trường hợp 1: Sự kiện đã bị hủy
            isCancelled -> {
                binding.btnDisabledEvent.visibility = View.VISIBLE
                binding.btnDisabledEvent.text = "Sự kiện đã bị hủy"
            }

            // Trường hợp 2: Người dùng ĐÃ ĐĂNG KÝ
            isRegistered -> {
                binding.layoutRegisteredActions.visibility = View.VISIBLE
                binding.btnViewTicket.setOnClickListener {
                    val bundle = bundleOf(
                        "eventId" to event.id,
                        "ticketCode" to (registration.ticketCode)
                    )
                    findNavController().navigate(R.id.ticketFragment, bundle)
                }
                binding.btnCancelRegistration.setOnClickListener {
                    showCancelConfirmationDialog()
                }
            }

            // Trường hợp 3: Sự kiện HẾT CHỖ hoặc ĐÃ ĐÓNG ĐĂNG KÝ -> Chuyển sang Waitlist
            isFull -> {
                binding.layoutWaitlistActions.visibility = View.VISIBLE
                binding.tvClosedReason.text = if (event.registeredCount >= event.capacity)
                    "Sự kiện đã hết chỗ đăng ký chính thức (${event.capacity}/${event.capacity})."
                else
                    "Ban tổ chức đã đóng đăng ký chính thức."

                binding.btnJoinWaitlistAction.setOnClickListener {
                    val bundle = bundleOf("eventId" to event.id)
                    findNavController().navigate(R.id.waitlistJoinFragment, bundle)
                }
            }

            // Trường hợp 4: Chưa đăng ký và còn chỗ
            else -> {
                binding.btnRegisterEvent.visibility = View.VISIBLE
                binding.btnRegisterEvent.setOnClickListener {
                    viewModel.registerCurrentEvent { newReg ->
                        val bundle = bundleOf(
                            "eventId" to event.id,
                            "ticketCode" to newReg.ticketCode
                        )
                        findNavController().navigate(R.id.ticketFragment, bundle)
                    }
                }
            }
        }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận hủy đăng ký")
            .setMessage("Bạn có chắc chắn muốn hủy đăng ký sự kiện này không? Vé QR của bạn sẽ bị vô hiệu và chỗ ngồi sẽ được nhường cho người tiếp theo trong danh sách chờ.")
            .setPositiveButton("Hủy đăng ký") { _, _ ->
                viewModel.cancelCurrentRegistration()
            }
            .setNegativeButton("Giữ vé", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
