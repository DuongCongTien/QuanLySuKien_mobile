package com.group29.eventapp.presentation.assistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group29.eventapp.R
import com.group29.eventapp.common.UiState
import com.group29.eventapp.databinding.FragmentAiAssistantBinding
import com.group29.eventapp.presentation.assistant.adapter.ChatMessageAdapter
import kotlinx.coroutines.launch

class AiAssistantFragment : Fragment() {

    private var _binding: FragmentAiAssistantBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AiAssistantViewModel by viewModels()
    private lateinit var chatAdapter: ChatMessageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiAssistantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupInput()
        setupSuggestionChips()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.rvChat.apply {
            val layoutMgr = LinearLayoutManager(requireContext())
            layoutMgr.stackFromEnd = true
            layoutManager = layoutMgr
            adapter = chatAdapter
        }
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.askQuestion(text)
                binding.etMessage.text?.clear()
            }
        }

        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.btnSend.performClick()
                true
            } else false
        }
    }

    private fun setupSuggestionChips() {
        binding.chipWifi.setOnClickListener {
            viewModel.askQuestion(getString(R.string.assistant_chip_wifi))
        }
        binding.chipSchedule.setOnClickListener {
            viewModel.askQuestion(getString(R.string.assistant_chip_schedule))
        }
        binding.chipFood.setOnClickListener {
            viewModel.askQuestion(getString(R.string.assistant_chip_food))
        }
        binding.chipCheckIn.setOnClickListener {
            viewModel.askQuestion(getString(R.string.assistant_chip_checkin))
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.messagesState.collect { state ->
                        if (state is UiState.Success) {
                            chatAdapter.submitList(state.data) {
                                binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                            }
                        }
                    }
                }

                launch {
                    viewModel.isAnswering.collect { isAnswering ->
                        binding.btnSend.isEnabled = !isAnswering
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
