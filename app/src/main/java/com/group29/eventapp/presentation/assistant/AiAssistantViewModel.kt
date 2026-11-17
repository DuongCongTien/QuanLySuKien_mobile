package com.group29.eventapp.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeAssistantRepository
import com.group29.eventapp.data.model.ChatMessageModel
import com.group29.eventapp.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiAssistantViewModel(
    private val assistantRepo: AssistantRepository = FakeAssistantRepository
) : ViewModel() {

    private val _messagesState = MutableStateFlow<UiState<List<ChatMessageModel>>>(UiState.Loading)
    val messagesState: StateFlow<UiState<List<ChatMessageModel>>> = _messagesState.asStateFlow()

    private val _isAnswering = MutableStateFlow(false)
    val isAnswering: StateFlow<Boolean> = _isAnswering.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            assistantRepo.getMessages().collect { list ->
                if (list.isEmpty()) {
                    _messagesState.value = UiState.Empty()
                } else {
                    _messagesState.value = UiState.Success(list)
                }
            }
        }
    }

    fun askQuestion(question: String) {
        if (question.isBlank() || _isAnswering.value) return

        viewModelScope.launch {
            _isAnswering.value = true
            try {
                assistantRepo.askAssistant(question)
            } catch (e: Exception) {
                // Khi có lỗi mạng giả lập
            } finally {
                _isAnswering.value = false
            }
        }
    }
}
