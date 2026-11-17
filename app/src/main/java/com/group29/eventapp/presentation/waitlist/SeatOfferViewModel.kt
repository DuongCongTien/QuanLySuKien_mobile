package com.group29.eventapp.presentation.waitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.mock.FakeWaitlistRepository
import com.group29.eventapp.data.model.RegistrationModel
import com.group29.eventapp.data.model.SeatOfferModel
import com.group29.eventapp.data.model.SeatOfferStatus
import com.group29.eventapp.domain.repository.WaitlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeatOfferViewModel(
    private val waitlistRepo: WaitlistRepository = FakeWaitlistRepository
) : ViewModel() {

    private val _offerState = MutableStateFlow<UiState<SeatOfferModel>>(UiState.Loading)
    val offerState: StateFlow<UiState<SeatOfferModel>> = _offerState.asStateFlow()

    private var currentOfferId: String = ""

    fun loadOffer(userId: String = "user_demo_attendee") {
        viewModelScope.launch {
            _offerState.value = UiState.Loading
            waitlistRepo.getActiveSeatOffer(userId).collect { offer ->
                if (offer == null) {
                    _offerState.value = UiState.Empty()
                } else {
                    currentOfferId = offer.id
                    _offerState.value = UiState.Success(offer)
                }
            }
        }
    }

    fun acceptOffer(onResult: (Boolean, RegistrationModel?, String) -> Unit) {
        viewModelScope.launch {
            val result = waitlistRepo.respondToSeatOffer(currentOfferId, true)
            if (result.isSuccess) {
                val reg = result.getOrNull()
                onResult(true, reg, "Đã nhận vé thành công! Chuyển tới vé điện tử.")
            } else {
                onResult(false, null, result.exceptionOrNull()?.message ?: "Không thể nhận chỗ.")
            }
        }
    }

    fun declineOffer(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = waitlistRepo.respondToSeatOffer(currentOfferId, false)
            if (result.isSuccess) {
                onResult(true, "Đã nhường chỗ cho người tiếp theo trong hàng đợi.")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Thao tác thất bại.")
            }
        }
    }
}
