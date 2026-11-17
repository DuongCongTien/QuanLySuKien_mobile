package com.group29.eventapp.presentation.memory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.mock.MockData
import com.group29.eventapp.databinding.FragmentEventMemoryBinding

class EventMemoryFragment : Fragment() {

    private var _binding: FragmentEventMemoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bindData()
    }

    private fun bindData() {
        val memory = MockData.sampleEventMemory

        binding.tvEventTitle.text = memory.eventTitle
        binding.tvCheckInTime.text = getString(R.string.memory_checkin_time, DateTimeUtils.formatDateTime(memory.checkInTime))
        binding.tvCompletedQuests.text = memory.completedQuestsCount.toString()
        binding.tvEarnedPoints.text = memory.earnedPoints.toString()
        binding.tvBadgeName.text = "Huy hiệu: ${memory.badgeName}"

        binding.tvCompanionsLabel.text = getString(R.string.memory_companions, memory.connectedUsers.size)
        binding.chipGroupCompanions.removeAllViews()
        memory.connectedUsers.forEach { user ->
            val chip = Chip(requireContext()).apply {
                text = user
                isCheckable = false
                isClickable = false
            }
            binding.chipGroupCompanions.addView(chip)
        }

        binding.switchPublic.isChecked = memory.isPublic
        binding.switchPublic.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Nhật ký kỷ niệm đã được đặt ở chế độ Công khai" else "Nhật ký kỷ niệm đã chuyển sang Riêng tư"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
