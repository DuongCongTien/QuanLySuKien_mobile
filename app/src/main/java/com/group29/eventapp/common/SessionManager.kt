package com.group29.eventapp.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class UserRole {
    ATTENDEE,
    ORGANIZER
}

object SessionManager {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserId = MutableStateFlow("user_demo_attendee")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUserName = MutableStateFlow("Nguyễn Văn A")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserEmail = MutableStateFlow("attendee@group29.vn")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.ATTENDEE)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Flag giả lập lỗi mạng để kiểm thử UI Error State
    private val _simulateNetworkError = MutableStateFlow(false)
    val simulateNetworkError: StateFlow<Boolean> = _simulateNetworkError.asStateFlow()

    // Quyền riêng tư hồ sơ
    private val _isProfileSearchable = MutableStateFlow(true)
    val isProfileSearchable: StateFlow<Boolean> = _isProfileSearchable.asStateFlow()

    fun loginAsAttendee() {
        _currentUserId.value = "user_demo_attendee"
        _currentUserName.value = "Nguyễn Văn A (Người tham dự)"
        _currentUserEmail.value = "attendee@group29.vn"
        _currentRole.value = UserRole.ATTENDEE
        _isLoggedIn.value = true
    }

    fun loginAsOrganizer() {
        _currentUserId.value = "user_demo_organizer"
        _currentUserName.value = "Trần Thị B (Ban tổ chức)"
        _currentUserEmail.value = "organizer@group29.vn"
        _currentRole.value = UserRole.ORGANIZER
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUserId.value = ""
        _currentRole.value = UserRole.ATTENDEE
    }

    fun setSimulateNetworkError(enabled: Boolean) {
        _simulateNetworkError.value = enabled
    }

    fun setProfileSearchable(enabled: Boolean) {
        _isProfileSearchable.value = enabled
    }

    fun isOrganizer(): Boolean = _currentRole.value == UserRole.ORGANIZER
}
