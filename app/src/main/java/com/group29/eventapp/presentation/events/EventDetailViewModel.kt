package com.group29.eventapp.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.mock.FakeRegistrationRepository
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.RegistrationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventDetailData(
    val event: EventModel,
    val userRegistration: RegistrationModel?
)

class EventDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<EventDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<EventDetailData>> = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private var currentEventId: String? = null

    fun loadEvent(eventId: String) {
        currentEventId = eventId
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val event = FakeEventRepository.getEventById(eventId)
                if (event == null) {
                    _uiState.value = UiState.Error(AppError.EVENT_CLOSED, "Không tìm thấy thông tin sự kiện.")
                    return@launch
                }

                val currentUserId = SessionManager.currentUserId.value
                val registration = FakeRegistrationRepository.getRegistration(currentUserId, eventId)

                _uiState.value = UiState.Success(
                    EventDetailData(event, registration)
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(AppError.NETWORK_SIMULATED_ERROR, e.message ?: "Lỗi tải sự kiện")
            }
        }
    }

    fun registerCurrentEvent(onSuccess: (RegistrationModel) -> Unit) {
        val eventId = currentEventId ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val userId = SessionManager.currentUserId.value
            val userName = SessionManager.currentUserName.value
            val userEmail = SessionManager.currentUserEmail.value

            val result = FakeRegistrationRepository.registerEvent(userId, userName, userEmail, eventId)
            result.onSuccess { newReg ->
                _actionMessage.value = "Đăng ký thành công!"
                loadEvent(eventId)
                onSuccess(newReg)
            }.onFailure { error ->
                _actionMessage.value = error.message ?: "Đăng ký thất bại"
                loadEvent(eventId)
            }
        }
    }

    fun cancelCurrentRegistration() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val reg = currentState.userRegistration ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val success = FakeRegistrationRepository.cancelRegistration(reg.id)
            if (success) {
                _actionMessage.value = "Đã hủy đăng ký sự kiện thành công. Ghế trống đã được chuyển cho người kế tiếp."
            } else {
                _actionMessage.value = "Hủy đăng ký thất bại."
            }
            loadEvent(reg.eventId)
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
