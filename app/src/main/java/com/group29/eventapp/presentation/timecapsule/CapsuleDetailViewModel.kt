package com.group29.eventapp.presentation.timecapsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeTimeCapsuleRepository
import com.group29.eventapp.data.model.TimeCapsuleModel
import com.group29.eventapp.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CapsuleDetailViewModel(
    private val capsuleRepo: TimeCapsuleRepository = FakeTimeCapsuleRepository
) : ViewModel() {

    private val _capsuleState = MutableStateFlow<UiState<TimeCapsuleModel>>(UiState.Loading)
    val capsuleState: StateFlow<UiState<TimeCapsuleModel>> = _capsuleState.asStateFlow()

    private var currentCapsuleId: String = ""

    fun loadCapsule(capsuleId: String) {
        currentCapsuleId = capsuleId
        viewModelScope.launch {
            _capsuleState.value = UiState.Loading
            val capsule = capsuleRepo.getCapsuleById(capsuleId)
            if (capsule == null) {
                _capsuleState.value = UiState.Empty()
            } else {
                _capsuleState.value = UiState.Success(capsule)
            }
        }
    }

    fun openCapsule(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = capsuleRepo.openCapsule(currentCapsuleId)
            if (result.isSuccess) {
                _capsuleState.value = UiState.Success(result.getOrThrow())
                onResult(true, "Mở khóa Hộp thời gian thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Chưa tới thời điểm mở khóa.")
            }
        }
    }
}
