package com.group29.eventapp.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.AppError
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeEventRepository
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchFilterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<EventModel>>> = _uiState.asStateFlow()

    var currentQuery: String = ""
        private set
    var currentCategory: String? = null
        private set
    var currentStatus: EventStatus? = null
        private set
    var currentIsOnline: Boolean? = null
        private set

    init {
        performSearch()
    }

    fun updateQuery(query: String) {
        currentQuery = query
        performSearch()
    }

    fun applyFilters(category: String?, status: EventStatus?, isOnline: Boolean?) {
        currentCategory = category
        currentStatus = status
        currentIsOnline = isOnline
        performSearch()
    }

    fun resetFilters() {
        currentQuery = ""
        currentCategory = null
        currentStatus = null
        currentIsOnline = null
        performSearch()
    }

    fun performSearch() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // // MOCK: Độ trễ tìm kiếm & lọc
            delay(600)

            if (SessionManager.simulateNetworkError.value) {
                _uiState.value = UiState.Error(
                    AppError.NETWORK_SIMULATED_ERROR,
                    AppError.NETWORK_SIMULATED_ERROR.defaultMessage
                )
                return@launch
            }

            FakeEventRepository.searchEvents(
                query = currentQuery,
                category = currentCategory,
                status = currentStatus,
                isOnline = currentIsOnline
            ).catch {
                _uiState.value = UiState.Error(AppError.NETWORK_SIMULATED_ERROR, it.message ?: "Lỗi tìm kiếm")
            }.collect { events ->
                // Lọc bỏ sự kiện Draft đối với người tham gia
                val visibleEvents = events.filter { it.status != EventStatus.Draft }
                if (visibleEvents.isEmpty()) {
                    _uiState.value = UiState.Empty("Không tìm thấy sự kiện nào phù hợp với điều kiện tìm kiếm và bộ lọc của bạn.")
                } else {
                    _uiState.value = UiState.Success(visibleEvents)
                }
            }
        }
    }
}
