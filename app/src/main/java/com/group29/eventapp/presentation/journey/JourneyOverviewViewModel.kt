package com.group29.eventapp.presentation.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeJourneyRepository
import com.group29.eventapp.data.model.UserJourneyModel
import com.group29.eventapp.data.model.UserJourneyStatus
import com.group29.eventapp.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JourneyOverviewViewModel(
    private val journeyRepo: JourneyRepository = FakeJourneyRepository
) : ViewModel() {

    private val _journeyState = MutableStateFlow<UiState<UserJourneyModel>>(UiState.Loading)
    val journeyState: StateFlow<UiState<UserJourneyModel>> = _journeyState.asStateFlow()

    private var currentEventId: String = "event_2"

    fun loadJourney(eventId: String = "event_2", userId: String = "user_demo_attendee") {
        currentEventId = eventId
        viewModelScope.launch {
            _journeyState.value = UiState.Loading
            journeyRepo.getUserJourney(eventId, userId).collect { journey ->
                if (journey == null) {
                    _journeyState.value = UiState.Empty()
                } else {
                    _journeyState.value = UiState.Success(journey)
                }
            }
        }
    }

    fun simulateScheduleChange() {
        viewModelScope.launch {
            journeyRepo.simulateScheduleChange(currentEventId)
        }
    }
}
