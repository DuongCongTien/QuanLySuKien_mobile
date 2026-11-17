package com.group29.eventapp.presentation.timecapsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeTimeCapsuleRepository
import com.group29.eventapp.data.model.TimeCapsuleModel
import com.group29.eventapp.data.model.TimeCapsuleStatus
import com.group29.eventapp.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CapsuleVaultViewModel(
    private val capsuleRepo: TimeCapsuleRepository = FakeTimeCapsuleRepository
) : ViewModel() {

    private val _capsulesState = MutableStateFlow<UiState<List<TimeCapsuleModel>>>(UiState.Loading)
    val capsulesState: StateFlow<UiState<List<TimeCapsuleModel>>> = _capsulesState.asStateFlow()

    private var allCapsules: List<TimeCapsuleModel> = emptyList()
    private var currentTabIndex: Int = 0

    fun loadVault(userId: String = "user_demo_attendee") {
        viewModelScope.launch {
            _capsulesState.value = UiState.Loading
            capsuleRepo.getCapsules(userId).collect { list ->
                allCapsules = list
                applyFilter()
            }
        }
    }

    fun setTabIndex(index: Int) {
        currentTabIndex = index
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = when (currentTabIndex) {
            0 -> allCapsules.filter { it.status == TimeCapsuleStatus.Locked || it.status == TimeCapsuleStatus.Sealed }
            1 -> allCapsules.filter { it.status == TimeCapsuleStatus.AvailableToOpen }
            2 -> allCapsules.filter { it.status == TimeCapsuleStatus.Opened }
            3 -> allCapsules.filter { it.status == TimeCapsuleStatus.Archived }
            else -> allCapsules
        }

        if (filtered.isEmpty()) {
            _capsulesState.value = UiState.Empty()
        } else {
            _capsulesState.value = UiState.Success(filtered)
        }
    }
}
