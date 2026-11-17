package com.group29.eventapp.presentation.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeJourneyRepository
import com.group29.eventapp.data.model.JourneyStepModel
import com.group29.eventapp.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JourneyStepDetailViewModel(
    private val journeyRepo: JourneyRepository = FakeJourneyRepository
) : ViewModel() {

    private val _stepState = MutableStateFlow<UiState<JourneyStepModel>>(UiState.Loading)
    val stepState: StateFlow<UiState<JourneyStepModel>> = _stepState.asStateFlow()

    private var currentStepId: String = ""
    private var currentEventId: String = "event_2"

    fun loadStep(stepId: String, eventId: String = "event_2") {
        currentStepId = stepId
        currentEventId = eventId
        viewModelScope.launch {
            _stepState.value = UiState.Loading
            journeyRepo.getUserJourney(eventId, "user_demo_attendee").collect { journey ->
                val step = journey?.steps?.find { it.id == stepId }
                if (step == null) {
                    _stepState.value = UiState.Empty()
                } else {
                    _stepState.value = UiState.Success(step)
                }
            }
        }
    }

    fun checkIn(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = journeyRepo.checkInStep(currentStepId)
            if (result.isSuccess) {
                onResult(true, "Check-in thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Check-in thất bại")
            }
        }
    }

    fun completeStep(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = journeyRepo.completeStep(currentStepId)
            if (result.isSuccess) {
                onResult(true, "Hoàn thành bước hành trình!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Chưa thể hoàn thành bước")
            }
        }
    }

    fun skipStep(reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = journeyRepo.skipStep(currentStepId, reason)
            if (result.isSuccess) {
                onResult(true, "Đã bỏ qua bước này")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Không thể bỏ qua")
            }
        }
    }
}
