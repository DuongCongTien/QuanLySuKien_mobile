package com.group29.eventapp.presentation.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.data.mock.FakeJourneyRepository
import com.group29.eventapp.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JourneySetupViewModel(
    private val journeyRepo: JourneyRepository = FakeJourneyRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun createJourney(
        eventId: String = "event_2",
        userId: String = "user_demo_attendee",
        goal: String,
        topics: List<String>,
        availableTime: String,
        priority: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = journeyRepo.setupJourney(eventId, userId, goal, topics, availableTime, priority)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
