package com.group29.eventapp.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.data.model.RegistrationStatus
import com.group29.eventapp.presentation.events.adapter.MyEventItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class MyEventsData(
    val upcoming: List<MyEventItem>,
    val attended: List<MyEventItem>,
    val cancelled: List<MyEventItem>
)

class MyEventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MyEventsData>>(UiState.Loading)
    val uiState: StateFlow<UiState<MyEventsData>> = _uiState.asStateFlow()

    init {
        loadMyEvents()
    }

    fun loadMyEvents() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // // MOCK: Độ trễ tải danh sách vé sự kiện của người dùng
            delay(650)

            if (SessionManager.simulateNetworkError.value) {
                _uiState.value = UiState.Error(
                    AppError.NETWORK_SIMULATED_ERROR,
                    AppError.NETWORK_SIMULATED_ERROR.defaultMessage
                )
                return@launch
            }

            val userId = SessionManager.currentUserId.value
            combine(
                FakeRegistrationRepository.getUserRegistrations(userId),
                FakeEventRepository.getEvents()
            ) { registrations, events ->
                val eventMap = events.associateBy { it.id }
                val items = registrations.mapNotNull { reg ->
                    val event = eventMap[reg.eventId]
                    if (event != null) MyEventItem(reg, event) else null
                }

                val upcoming = items.filter {
                    it.registration.status == RegistrationStatus.Registered &&
                            (it.event.status == EventStatus.Published || (it.event.status == EventStatus.Ongoing && it.registration.checkInStatus == CheckInStatus.NotCheckedIn))
                }

                val attended = items.filter {
                    it.registration.status == RegistrationStatus.Registered &&
                            (it.event.status == EventStatus.Completed || it.registration.checkInStatus == CheckInStatus.CheckedIn)
                }

                val cancelled = items.filter {
                    it.registration.status == RegistrationStatus.Cancelled || it.event.status == EventStatus.Cancelled
                }

                MyEventsData(upcoming, attended, cancelled)
            }.collect { data ->
                _uiState.value = UiState.Success(data)
            }
        }
    }
}
