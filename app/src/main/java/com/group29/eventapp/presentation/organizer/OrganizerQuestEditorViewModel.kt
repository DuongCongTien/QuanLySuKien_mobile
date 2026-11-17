package com.group29.eventapp.presentation.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeQuestRepository
import com.group29.eventapp.data.model.QuestModel
import com.group29.eventapp.data.model.QuestStatus
import com.group29.eventapp.domain.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganizerQuestEditorViewModel(
    private val questRepo: QuestRepository = FakeQuestRepository
) : ViewModel() {

    private val _questsState = MutableStateFlow<UiState<List<QuestModel>>>(UiState.Loading)
    val questsState: StateFlow<UiState<List<QuestModel>>> = _questsState.asStateFlow()

    private var currentEventId: String = "evt_01"

    fun loadQuests(eventId: String = "evt_01") {
        currentEventId = eventId
        viewModelScope.launch {
            _questsState.value = UiState.Loading
            questRepo.getQuests(eventId).collect { list ->
                if (list.isEmpty()) {
                    _questsState.value = UiState.Empty()
                } else {
                    _questsState.value = UiState.Success(list)
                }
            }
        }
    }

    fun createQuest(
        title: String,
        desc: String,
        points: Int,
        maxAttempts: Int,
        verification: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val newQuest = QuestModel(
                id = "quest_${System.currentTimeMillis()}",
                eventId = currentEventId,
                title = title,
                description = desc,
                points = points,
                maxAttempts = maxAttempts,
                verificationMethod = verification,
                status = QuestStatus.Active
            )
            val success = questRepo.createQuest(newQuest)
            if (success) {
                onResult(true, "Tạo nhiệm vụ thành công!")
            } else {
                onResult(false, "Không thể tạo nhiệm vụ.")
            }
        }
    }
}
