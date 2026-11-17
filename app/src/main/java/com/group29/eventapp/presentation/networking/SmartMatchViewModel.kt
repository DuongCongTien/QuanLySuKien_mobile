package com.group29.eventapp.presentation.networking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeNetworkingRepository
import com.group29.eventapp.data.model.ConnectionStatus
import com.group29.eventapp.data.model.NetworkingMatchModel
import com.group29.eventapp.domain.repository.NetworkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SmartMatchViewModel(
    private val networkingRepo: NetworkingRepository = FakeNetworkingRepository
) : ViewModel() {

    private val _suggestedMatchesState = MutableStateFlow<UiState<List<NetworkingMatchModel>>>(UiState.Loading)
    val suggestedMatchesState: StateFlow<UiState<List<NetworkingMatchModel>>> = _suggestedMatchesState.asStateFlow()

    private val _invitationsState = MutableStateFlow<UiState<List<NetworkingMatchModel>>>(UiState.Loading)
    val invitationsState: StateFlow<UiState<List<NetworkingMatchModel>>> = _invitationsState.asStateFlow()

    private val _isPrivacyEnabled = MutableStateFlow(true)
    val isPrivacyEnabled: StateFlow<Boolean> = _isPrivacyEnabled.asStateFlow()

    fun loadData(eventId: String = "evt_01", userId: String = "user_demo") {
        viewModelScope.launch {
            _suggestedMatchesState.value = UiState.Loading
            networkingRepo.getSuggestedMatches(eventId, userId).collect { list ->
                if (list.isEmpty()) {
                    _suggestedMatchesState.value = UiState.Empty()
                } else {
                    _suggestedMatchesState.value = UiState.Success(list)
                }
            }
        }

        viewModelScope.launch {
            _invitationsState.value = UiState.Loading
            networkingRepo.getPendingInvitations().collect { list ->
                if (list.isEmpty()) {
                    _invitationsState.value = UiState.Empty()
                } else {
                    _invitationsState.value = UiState.Success(list)
                }
            }
        }
    }

    fun setPrivacyEnabled(enabled: Boolean) {
        _isPrivacyEnabled.value = enabled
    }

    fun sendInvitation(match: NetworkingMatchModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = networkingRepo.sendInvitation(match.user.id)
            onResult(success)
        }
    }

    fun respondToInvitation(match: NetworkingMatchModel, isAccepted: Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = networkingRepo.respondToInvitation(match.user.id, isAccepted)
            onResult(success)
        }
    }
}
