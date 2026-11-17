package com.group29.eventapp.presentation.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.group29.eventapp.R
import com.group29.eventapp.databinding.FragmentJourneySetupBinding
import kotlinx.coroutines.launch

class JourneySetupFragment : Fragment() {

    private var _binding: FragmentJourneySetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JourneySetupViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJourneySetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId") ?: "event_2"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreateJourney.setOnClickListener {
            val selectedGoalId = binding.radioGroupGoals.checkedRadioButtonId
            val goal = view.findViewById<RadioButton>(selectedGoalId)?.text?.toString() ?: "Học hỏi & Nâng cao kiến thức"

            val topics = mutableListOf<String>()
            for (i in 0 until binding.chipGroupTopics.childCount) {
                val chip = binding.chipGroupTopics.getChildAt(i) as? Chip
                if (chip?.isChecked == true) {
                    topics.add(chip.text.toString())
                }
            }

            val timeWindow = binding.etTimeWindow.text.toString().trim()
            val priority = binding.etPriority.text.toString().trim()

            viewModel.createJourney(eventId, "user_demo_attendee", goal, topics, timeWindow, priority) {
                // Sang Màn 22: Tổng quan hành trình
                val bundle = Bundle().apply { putString("eventId", eventId) }
                findNavController().navigate(R.id.journeyOverviewFragment, bundle)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { loading ->
                    binding.progressLoading.visibility = if (loading) View.VISIBLE else View.GONE
                    binding.btnCreateJourney.isEnabled = !loading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
