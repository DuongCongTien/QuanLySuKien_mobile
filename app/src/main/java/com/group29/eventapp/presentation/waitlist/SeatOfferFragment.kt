package com.group29.eventapp.presentation.waitlist

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.common.UiState
import com.group29.eventapp.data.model.SeatOfferModel
import com.group29.eventapp.data.model.SeatOfferStatus
import com.group29.eventapp.databinding.FragmentSeatOfferBinding
import kotlinx.coroutines.launch

class SeatOfferFragment : Fragment() {

    private var _binding: FragmentSeatOfferBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SeatOfferViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeatOfferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupActions()
        observeViewModel()

        viewModel.loadOffer()
    }

    private fun setupActions() {
        binding.btnAcceptSeat.setOnClickListener {
            viewModel.acceptOffer { success, registration, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success && registration != null) {
                    // Chuyển tới Màn 8: TicketFragment
                    val bundle = Bundle().apply {
                        putString("ticketCode", registration.ticketCode)
                        putString("eventId", registration.eventId)
                    }
                    findNavController().navigate(R.id.ticketFragment, bundle)
                }
            }
        }

        binding.btnDeclineSeat.setOnClickListener {
            viewModel.declineOffer { success, msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerState.collect { state ->
                    if (state is UiState.Success) {
                        bindOffer(state.data)
                    }
                }
            }
        }
    }

    private fun bindOffer(offer: SeatOfferModel) {
        binding.tvEventTitle.text = offer.eventTitle
        startCountdown(offer.expiresAt)
    }

    private fun startCountdown(expiresAt: Long) {
        countDownTimer?.cancel()
        val remaining = expiresAt - System.currentTimeMillis()
        if (remaining <= 0) {
            handleExpired()
            return
        }

        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountdownTimer.text = DateTimeUtils.formatCountdown(millisUntilFinished)
            }
            override fun onFinish() {
                handleExpired()
            }
        }.start()
    }

    private fun handleExpired() {
        binding.tvCountdownTimer.text = "00:00 (Hết hạn)"
        binding.cardExpiredNotice.visibility = View.VISIBLE
        binding.btnAcceptSeat.isEnabled = false
        binding.btnAcceptSeat.text = "Lời mời đã hết hạn"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
