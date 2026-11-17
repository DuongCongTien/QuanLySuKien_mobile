package com.group29.eventapp.presentation.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.RegistrationModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TicketData(
    val event: EventModel,
    val registration: RegistrationModel
)

class TicketViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<TicketData>>(UiState.Loading)
    val uiState: StateFlow<UiState<TicketData>> = _uiState.asStateFlow()

    fun loadTicket(eventId: String?, ticketCode: String?) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // // MOCK: Độ trễ tải thông tin vé điện tử
            delay(600)

            if (SessionManager.simulateNetworkError.value) {
                _uiState.value = UiState.Error(
                    AppError.NETWORK_SIMULATED_ERROR,
                    AppError.NETWORK_SIMULATED_ERROR.defaultMessage
                )
                return@launch
            }

            try {
                val userId = SessionManager.currentUserId.value
                val registrations = FakeRegistrationRepository.getUserRegistrations(userId).first()

                val matchedReg = if (!ticketCode.isNullOrBlank()) {
                    registrations.find { it.ticketCode.equals(ticketCode, ignoreCase = true) }
                } else if (!eventId.isNullOrBlank()) {
                    registrations.find { it.eventId == eventId }
                } else {
                    registrations.firstOrNull()
                }

                if (matchedReg == null) {
                    _uiState.value = UiState.Error(AppError.EVENT_CLOSED, "Không tìm thấy thông tin vé của bạn cho sự kiện này.")
                    return@launch
                }

                val event = FakeEventRepository.getEventById(matchedReg.eventId)
                if (event == null) {
                    _uiState.value = UiState.Error(AppError.EVENT_CLOSED, "Sự kiện liên kết với vé không tồn tại.")
                    return@launch
                }

                _uiState.value = UiState.Success(TicketData(event, matchedReg))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(AppError.NETWORK_SIMULATED_ERROR, e.message ?: "Lỗi tải vé")
            }
        }
    }
}
