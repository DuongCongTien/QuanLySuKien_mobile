package com.group29.eventapp.presentation.waitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeWaitlistRepository
import com.group29.eventapp.data.model.WaitlistEntryModel
import com.group29.eventapp.data.model.WaitlistEntryStatus
import com.group29.eventapp.domain.repository.WaitlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganizerWaitlistViewModel(
    private val waitlistRepo: WaitlistRepository = FakeWaitlistRepository
) : ViewModel() {

    private val _entriesState = MutableStateFlow<UiState<List<WaitlistEntryModel>>>(UiState.Loading)
    val entriesState: StateFlow<UiState<List<WaitlistEntryModel>>> = _entriesState.asStateFlow()

    private var allEntries: List<WaitlistEntryModel> = emptyList()

    val countConfirmed: Int
        get() = allEntries.count { it.status == WaitlistEntryStatus.Registered }

    val countHeld: Int
        get() = allEntries.count { it.status == WaitlistEntryStatus.Offered }

    val countWaiting: Int
        get() = allEntries.count { it.status == WaitlistEntryStatus.Waiting }

    fun loadWaitlist(eventId: String = "event_4") {
        viewModelScope.launch {
            _entriesState.value = UiState.Loading
            waitlistRepo.getWaitlistEntries(eventId).collect { list ->
                allEntries = list
                if (list.isEmpty()) {
                    _entriesState.value = UiState.Empty()
                } else {
                    _entriesState.value = UiState.Success(list)
                }
            }
        }
    }
}
