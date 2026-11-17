package com.group29.eventapp.presentation.secretmission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeQuestRepository
import com.group29.eventapp.data.model.MissionAssignmentStatus
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.domain.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecretMissionViewModel(
    private val questRepo: QuestRepository = FakeQuestRepository
) : ViewModel() {

    private val _missionsState = MutableStateFlow<UiState<List<SecretMissionModel>>>(UiState.Loading)
    val missionsState: StateFlow<UiState<List<SecretMissionModel>>> = _missionsState.asStateFlow()

    private var allMissions: List<SecretMissionModel> = emptyList()
    private var currentTabIndex: Int = 0

    fun loadMissions(eventId: String = "event_2") {
        viewModelScope.launch {
            _missionsState.value = UiState.Loading
            questRepo.getSecretMissions(eventId).collect { list ->
                allMissions = list
                applyFilter()
            }
        }
    }

    fun setTabIndex(tabIndex: Int) {
        currentTabIndex = tabIndex
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = when (currentTabIndex) {
            0 -> allMissions.filter { it.assignmentStatus == MissionAssignmentStatus.Available || it.assignmentStatus == MissionAssignmentStatus.Locked }
            1 -> allMissions.filter { it.assignmentStatus == MissionAssignmentStatus.InProgress }
            2 -> allMissions.filter { it.assignmentStatus == MissionAssignmentStatus.PendingVerification }
            3 -> allMissions.filter { it.assignmentStatus == MissionAssignmentStatus.Completed }
            4 -> allMissions.filter { it.assignmentStatus == MissionAssignmentStatus.Expired || it.assignmentStatus == MissionAssignmentStatus.Rejected }
            else -> allMissions
        }

        if (filtered.isEmpty()) {
            _missionsState.value = UiState.Empty()
        } else {
            _missionsState.value = UiState.Success(filtered)
        }
    }
}
