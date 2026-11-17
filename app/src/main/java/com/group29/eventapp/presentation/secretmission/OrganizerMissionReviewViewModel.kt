package com.group29.eventapp.presentation.secretmission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeQuestRepository
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.domain.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganizerMissionReviewViewModel(
    private val questRepo: QuestRepository = FakeQuestRepository
) : ViewModel() {

    private val _reviewQueueState = MutableStateFlow<UiState<List<SecretMissionModel>>>(UiState.Loading)
    val reviewQueueState: StateFlow<UiState<List<SecretMissionModel>>> = _reviewQueueState.asStateFlow()

    private var currentEventId: String = "event_2"

    fun loadQueue(eventId: String = "event_2") {
        currentEventId = eventId
        viewModelScope.launch {
            _reviewQueueState.value = UiState.Loading
            questRepo.getSecretMissions(eventId).collect { list ->
                if (list.isEmpty()) {
                    _reviewQueueState.value = UiState.Empty()
                } else {
                    _reviewQueueState.value = UiState.Success(list)
                }
            }
        }
    }

    fun approveMission(missionId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = questRepo.reviewMission(missionId, true, null)
            if (result.isSuccess) {
                onResult(true, "Đã phê duyệt bằng chứng thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Phê duyệt thất bại.")
            }
        }
    }

    fun rejectMission(missionId: String, reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = questRepo.reviewMission(missionId, false, reason)
            if (result.isSuccess) {
                onResult(true, "Đã từ chối bằng chứng nhiệm vụ.")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Từ chối thất bại.")
            }
        }
    }
}
