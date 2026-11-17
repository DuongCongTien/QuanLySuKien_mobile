package com.group29.eventapp.presentation.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeQuestRepository
import com.group29.eventapp.data.model.QuestCompletionStatus
import com.group29.eventapp.data.model.QuestModel
import com.group29.eventapp.domain.repository.QuestRepository
import com.group29.eventapp.presentation.quest.adapter.LeaderboardUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestViewModel(
    private val questRepo: QuestRepository = FakeQuestRepository
) : ViewModel() {

    private val _questsState = MutableStateFlow<UiState<List<QuestModel>>>(UiState.Loading)
    val questsState: StateFlow<UiState<List<QuestModel>>> = _questsState.asStateFlow()

    private val _leaderboardUsers = MutableStateFlow<List<LeaderboardUser>>(emptyList())
    val leaderboardUsers: StateFlow<List<LeaderboardUser>> = _leaderboardUsers.asStateFlow()

    private var allQuests: List<QuestModel> = emptyList()
    private var currentFilterIndex = 0

    val totalPoints: Int
        get() = allQuests.filter { it.completionStatus == QuestCompletionStatus.Completed }.sumOf { it.points }

    val completedCount: Int
        get() = allQuests.count { it.completionStatus == QuestCompletionStatus.Completed }

    val totalCount: Int
        get() = allQuests.size

    fun loadQuests(eventId: String = "evt_01") {
        viewModelScope.launch {
            _questsState.value = UiState.Loading
            questRepo.getQuests(eventId).collect { list ->
                allQuests = list
                applyFilter()
            }
        }

        // Dữ liệu bảng xếp hạng mẫu
        _leaderboardUsers.value = listOf(
            LeaderboardUser(1, "Trần Hoàng Nam", "AI Researcher @ DaNang Tech", 420),
            LeaderboardUser(2, "Lê Thị Bích", "Product Manager @ FPT", 360),
            LeaderboardUser(3, "Nguyễn Văn An (Bạn)", "Mobile Developer @ Group 29", 250),
            LeaderboardUser(4, "Đặng Quang Huy", "Data Scientist @ Viettel", 210),
            LeaderboardUser(5, "Phạm Minh Trang", "UI/UX Designer", 180)
        )
    }

    fun setFilterIndex(index: Int) {
        currentFilterIndex = index
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = when (currentFilterIndex) {
            1 -> allQuests.filter { it.completionStatus == QuestCompletionStatus.InProgress }
            2 -> allQuests.filter { it.completionStatus == QuestCompletionStatus.PendingVerification }
            3 -> allQuests.filter { it.completionStatus == QuestCompletionStatus.Completed }
            4 -> allQuests.filter { it.completionStatus == QuestCompletionStatus.Rejected }
            else -> allQuests
        }

        if (filtered.isEmpty()) {
            _questsState.value = UiState.Empty()
        } else {
            _questsState.value = UiState.Success(filtered)
        }
    }
}
