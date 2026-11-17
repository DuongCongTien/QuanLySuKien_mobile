package com.group29.eventapp.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.MockData
import com.group29.eventapp.data.model.NotificationModel
import com.group29.eventapp.data.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(MockData.sampleNotifications)
    private val _notificationsState = MutableStateFlow<UiState<List<NotificationModel>>>(UiState.Loading)
    val notificationsState: StateFlow<UiState<List<NotificationModel>>> = _notificationsState.asStateFlow()

    private var currentFilterType: NotificationType? = null

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading
            applyFilter()
        }
    }

    fun setFilter(type: NotificationType?) {
        currentFilterType = type
        applyFilter()
    }

    fun markAsRead(notificationId: String) {
        val updated = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
        _notifications.value = updated
        applyFilter()
    }

    fun markAllAsRead() {
        val updated = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = updated
        applyFilter()
    }

    private fun applyFilter() {
        val list = if (currentFilterType != null) {
            _notifications.value.filter { it.type == currentFilterType }
        } else {
            _notifications.value
        }

        if (list.isEmpty()) {
            _notificationsState.value = UiState.Empty()
        } else {
            _notificationsState.value = UiState.Success(list)
        }
    }
}
