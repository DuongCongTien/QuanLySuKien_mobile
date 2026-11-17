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

class SecretMissionDetailViewModel(
    private val questRepo: QuestRepository = FakeQuestRepository
) : ViewModel() {

    private val _missionState = MutableStateFlow<UiState<SecretMissionModel>>(UiState.Loading)
    val missionState: StateFlow<UiState<SecretMissionModel>> = _missionState.asStateFlow()

    private var currentMissionId: String = "sm_1"
    private var currentEventId: String = "event_2"

    fun loadMission(missionId: String, eventId: String = "event_2") {
        currentMissionId = missionId
        currentEventId = eventId
        viewModelScope.launch {
            _missionState.value = UiState.Loading
            questRepo.getSecretMissions(eventId).collect { list ->
                val mission = list.find { it.id == missionId }
                if (mission == null) {
                    _missionState.value = UiState.Empty()
                } else {
                    _missionState.value = UiState.Success(mission)
                }
            }
        }
    }

    fun unlockHint(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = questRepo.unlockMissionHint(currentMissionId)
            if (result.isSuccess) {
                onResult(true, "Mở khóa gợi ý thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Đã mở hết gợi ý!")
            }
        }
    }

    fun submitEvidence(evidence: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = questRepo.submitMissionEvidence(currentMissionId, evidence)
            if (result.isSuccess) {
                onResult(true, "Đã gửi bằng chứng! Đang chờ Ban tổ chức duyệt.")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Gửi bằng chứng thất bại.")
            }
        }
    }

    fun claimReward(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = questRepo.claimMissionReward(currentMissionId)
            if (result.isSuccess) {
                onResult(true, "Chúc mừng! Bạn đã nhận thưởng thành công!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Không thể nhận thưởng.")
            }
        }
    }
}
