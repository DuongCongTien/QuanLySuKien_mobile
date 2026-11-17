package com.group29.eventapp.presentation.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.data.model.RegistrationModel
import com.group29.eventapp.data.model.RegistrationStatus
import com.group29.eventapp.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AttendeeFilter {
    ALL, CHECKED_IN, NOT_CHECKED_IN, CANCELLED
}

class OrganizerAttendeesViewModel(
    private val registrationRepo: RegistrationRepository = FakeRegistrationRepository
) : ViewModel() {

    private val _attendeesState = MutableStateFlow<UiState<List<RegistrationModel>>>(UiState.Loading)
    val attendeesState: StateFlow<UiState<List<RegistrationModel>>> = _attendeesState.asStateFlow()

    private var currentEventId: String = "evt_01"
    private var allAttendees: List<RegistrationModel> = emptyList()
    private var searchQuery: String = ""
    private var currentFilter: AttendeeFilter = AttendeeFilter.ALL

    fun loadAttendees(eventId: String) {
        currentEventId = eventId
        viewModelScope.launch {
            _attendeesState.value = UiState.Loading
            registrationRepo.getEventAttendees(eventId).collect { list ->
                allAttendees = list
                applyFilterAndSearch()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query.trim()
        applyFilterAndSearch()
    }

    fun onFilterChanged(filter: AttendeeFilter) {
        currentFilter = filter
        applyFilterAndSearch()
    }

    fun manualCheckIn(registration: RegistrationModel, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = registrationRepo.verifyAndCheckInTicket(registration.ticketCode)
            if (result.isSuccess) {
                onResult(true, "Check-in thành công cho ${registration.userName}!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Check-in thất bại")
            }
        }
    }

    private fun applyFilterAndSearch() {
        var filtered = allAttendees

        when (currentFilter) {
            AttendeeFilter.ALL -> {}
            AttendeeFilter.CHECKED_IN -> {
                filtered = filtered.filter { it.checkInStatus == CheckInStatus.CheckedIn && it.status == RegistrationStatus.Registered }
            }
            AttendeeFilter.NOT_CHECKED_IN -> {
                filtered = filtered.filter { it.checkInStatus == CheckInStatus.NotCheckedIn && it.status == RegistrationStatus.Registered }
            }
            AttendeeFilter.CANCELLED -> {
                filtered = filtered.filter { it.status == RegistrationStatus.Cancelled }
            }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.userName.contains(searchQuery, ignoreCase = true) ||
                it.userEmail.contains(searchQuery, ignoreCase = true) ||
                it.ticketCode.contains(searchQuery, ignoreCase = true)
            }
        }

        if (filtered.isEmpty()) {
            _attendeesState.value = UiState.Empty()
        } else {
            _attendeesState.value = UiState.Success(filtered)
        }
    }
}
