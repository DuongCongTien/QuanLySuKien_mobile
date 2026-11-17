package com.group29.eventapp.presentation.timecapsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.data.mock.FakeTimeCapsuleRepository
import com.group29.eventapp.data.model.TimeCapsuleModel
import com.group29.eventapp.data.model.TimeCapsuleStatus
import com.group29.eventapp.data.model.TimeCapsuleType
import com.group29.eventapp.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CapsuleCreateViewModel(
    private val capsuleRepo: TimeCapsuleRepository = FakeTimeCapsuleRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun createAndSealCapsule(
        title: String,
        message: String,
        recipient: String,
        type: TimeCapsuleType,
        isPublic: Boolean,
        unlockAt: Long,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val capsule = TimeCapsuleModel(
                id = "tc_${System.currentTimeMillis()}",
                eventId = "event_2",
                creatorId = "user_demo_attendee",
                creatorName = "Nguyễn Văn A",
                recipientName = recipient,
                title = title,
                message = message,
                type = type,
                status = TimeCapsuleStatus.Sealed,
                createdAt = System.currentTimeMillis(),
                unlockAt = unlockAt,
                isPublic = isPublic
            )

            val result = capsuleRepo.createCapsule(capsule)
            _isLoading.value = false
            if (result.isSuccess) {
                onResult(true, "Đã niêm phong Hộp thời gian thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Không thể tạo hộp thời gian.")
            }
        }
    }
}
