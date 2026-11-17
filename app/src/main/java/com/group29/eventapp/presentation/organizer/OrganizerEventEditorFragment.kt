package com.group29.eventapp.presentation.organizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.databinding.FragmentOrganizerEventEditorBinding
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OrganizerEventEditorFragment : Fragment() {

    private var _binding: FragmentOrganizerEventEditorBinding? = null
    private val binding get() = _binding!!

    private var eventId: String? = null
    private var existingEvent: EventModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString("eventId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrganizerEventEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if (eventId != null) {
            loadExistingEvent(eventId!!)
        }

        setupActionButtons()
    }

    private fun loadExistingEvent(id: String) {
        lifecycleScope.launch {
            existingEvent = FakeEventRepository.getEventById(id)
            existingEvent?.let { ev ->
                with(binding) {
                    toolbar.title = "Chỉnh sửa: ${ev.title}"
                    etEventTitle.setText(ev.title)
                    etEventDesc.setText(ev.description)
                    etEventCategory.setText(ev.category)
                    etEventCapacity.setText(ev.capacity.toString())
                    etEventLocation.setText(ev.location)
                    switchIsOnline.isChecked = ev.isOnline
                    etEventRequirements.setText(ev.requirements)
                }
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnSaveDraft.setOnClickListener {
            saveEvent(EventStatus.Draft, "Đã lưu bản nháp sự kiện thành công!")
        }

        binding.btnPublishEvent.setOnClickListener {
            saveEvent(EventStatus.Published, "Đã công bố sự kiện tới người tham dự!")
        }

        binding.btnCloseRegistration.setOnClickListener {
            saveEvent(EventStatus.RegistrationClosed, "Đã đóng đăng ký sự kiện!")
        }

        binding.btnCancelEvent.setOnClickListener {
            saveEvent(EventStatus.Cancelled, "Đã cập nhật trạng thái: Hủy sự kiện!")
        }
    }

    private fun saveEvent(targetStatus: EventStatus, successMessage: String) {
        val title = binding.etEventTitle.text?.toString()?.trim().orEmpty()
        val desc = binding.etEventDesc.text?.toString()?.trim().orEmpty()
        val category = binding.etEventCategory.text?.toString()?.trim().orEmpty()
        val capacity = binding.etEventCapacity.text?.toString()?.toIntOrNull() ?: 100
        val location = binding.etEventLocation.text?.toString()?.trim().orEmpty()
        val isOnline = binding.switchIsOnline.isChecked
        val requirements = binding.etEventRequirements.text?.toString()?.trim().orEmpty()

        if (title.isEmpty()) {
            binding.tilEventTitle.error = "Vui lòng nhập tên sự kiện"
            return
        }

        lifecycleScope.launch {
            val now = System.currentTimeMillis()
            val eventToSave = existingEvent?.copy(
                title = title,
                description = desc,
                category = category,
                capacity = capacity,
                location = location,
                isOnline = isOnline,
                requirements = requirements,
                status = targetStatus
            ) ?: EventModel(
                id = "event_${now}",
                title = title,
                description = desc,
                category = category,
                location = location,
                isOnline = isOnline,
                startTime = now + TimeUnit.HOURS.toMillis(48),
                endTime = now + TimeUnit.HOURS.toMillis(56),
                capacity = capacity,
                registeredCount = 0,
                status = targetStatus,
                organizerName = SessionManager.currentUserName.value,
                speakerNames = listOf("Đang cập nhật"),
                requirements = requirements
            )

            if (existingEvent != null) {
                FakeEventRepository.updateEvent(eventToSave)
            } else {
                FakeEventRepository.createEvent(eventToSave)
            }

            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
