package com.group29.eventapp.presentation.waitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeWaitlistRepository
import com.group29.eventapp.data.model.SeatOfferModel
import com.group29.eventapp.data.model.SeatOfferStatus
import com.group29.eventapp.data.model.WaitlistEntryModel
import com.group29.eventapp.domain.repository.WaitlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WaitlistViewModel(
    private val waitlistRepo: WaitlistRepository = FakeWaitlistRepository
) : ViewModel() {

    private val _entryState = MutableStateFlow<UiState<WaitlistEntryModel>>(UiState.Loading)
    val entryState: StateFlow<UiState<WaitlistEntryModel>> = _entryState.asStateFlow()

    private val _activeOffer = MutableStateFlow<SeatOfferModel?>(null)
    val activeOffer: StateFlow<SeatOfferModel?> = _activeOffer.asStateFlow()

    private var currentEntryId: String = ""

    fun loadStatus(eventId: String = "event_4", userId: String = "user_demo_attendee") {
        viewModelScope.launch {
            _entryState.value = UiState.Loading
            val entry = waitlistRepo.getUserWaitlistEntry(eventId, userId)
            if (entry != null) {
                currentEntryId = entry.id
                _entryState.value = UiState.Success(entry)
            } else {
                _entryState.value = UiState.Empty()
            }
        }

        viewModelScope.launch {
            waitlistRepo.getActiveSeatOffer(userId).collect { offer ->
                if (offer != null && offer.status == SeatOfferStatus.Pending) {
                    _activeOffer.value = offer
                } else {
                    _activeOffer.value = null
                }
            }
        }
    }

    fun leaveWaitlist(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = waitlistRepo.leaveWaitlist(currentEntryId)
            if (success) {
                _entryState.value = UiState.Empty()
                onResult(true, "Đã rời danh sách chờ thành công.")
            } else {
                onResult(false, "Không thể rời danh sách chờ.")
            }
        }
    }
}
