package com.group29.eventapp.presentation.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.RegistrationStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class OrganizerDashboardData(
    val totalRegistered: Int,
    val totalCheckedIn: Int,
    val totalPending: Int,
    val totalCancelled: Int,
    val attendanceRate: Float,
    val managedEvents: List<EventModel>
)

class OrganizerDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<OrganizerDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<OrganizerDashboardData>> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // // MOCK: Độ trễ tính toán số liệu thống kê Dashboard BTC
            delay(650)

            if (SessionManager.simulateNetworkError.value) {
                _uiState.value = UiState.Error(
                    AppError.NETWORK_SIMULATED_ERROR,
                    AppError.NETWORK_SIMULATED_ERROR.defaultMessage
                )
                return@launch
            }

            FakeEventRepository.getEvents().collect { events ->
                var totalRegistered = 0
                var totalCheckedIn = 0
                var totalCancelled = 0

                events.forEach { ev ->
                    totalRegistered += ev.registeredCount
                }

                // Mô phỏng số liệu check-in thực tế
                totalCheckedIn = (totalRegistered * 0.72f).toInt()
                val totalPending = maxOf(0, totalRegistered - totalCheckedIn)
                totalCancelled = 28

                val rate = if (totalRegistered > 0) (totalCheckedIn * 100f) / totalRegistered else 0f

                _uiState.value = UiState.Success(
                    OrganizerDashboardData(
                        totalRegistered = totalRegistered,
                        totalCheckedIn = totalCheckedIn,
                        totalPending = totalPending,
                        totalCancelled = totalCancelled,
                        attendanceRate = rate,
                        managedEvents = events // BTC thấy toàn bộ sự kiện bao gồm cả Draft
                    )
                )
            }
        }
    }
}
