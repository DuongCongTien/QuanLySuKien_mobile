package com.group29.eventapp.presentation.home

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

data class HomeData(
    val featuredEvents: List<EventModel>,
    val recommendedEvents: List<EventModel>,
    val upcomingEvents: List<EventModel>
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    private var selectedCategory: String = "Tất cả"

    init {
        loadHomeData()
    }

    fun setCategory(category: String) {
        selectedCategory = category
        loadHomeData()
    }

    fun refresh() {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // // MOCK: Độ trễ tải trang chủ
            delay(600)

            if (SessionManager.simulateNetworkError.value) {
                _uiState.value = UiState.Error(
                    AppError.NETWORK_SIMULATED_ERROR,
                    AppError.NETWORK_SIMULATED_ERROR.defaultMessage
                )
                return@launch
            }

            FakeEventRepository.getEvents()
                .catch {
                    _uiState.value = UiState.Error(AppError.NETWORK_SIMULATED_ERROR, it.message ?: "Lỗi tải dữ liệu")
                }
                .collect { allEvents ->
                    // Lọc theo danh mục nếu có
                    val filtered = if (selectedCategory == "Tất cả") {
                        allEvents.filter { it.status != EventStatus.Draft } // Người dùng không thấy bản nháp
                    } else {
                        allEvents.filter { it.category == selectedCategory && it.status != EventStatus.Draft }
                    }

                    if (filtered.isEmpty()) {
                        _uiState.value = UiState.Empty("Không có sự kiện nào thuộc danh mục này")
                    } else {
                        val featured = filtered.filter { it.isFeatured }
                        val recommended = filtered.filter { it.status == EventStatus.Ongoing || it.status == EventStatus.Published }
                        val upcoming = filtered.filter { it.status == EventStatus.Published || it.status == EventStatus.RegistrationClosed }

                        _uiState.value = UiState.Success(
                            HomeData(
                                featuredEvents = if (featured.isNotEmpty()) featured else filtered.take(2),
                                recommendedEvents = if (recommended.isNotEmpty()) recommended else filtered,
                                upcomingEvents = if (upcoming.isNotEmpty()) upcoming else filtered
                            )
                        )
                    }
                }
        }
    }
}
