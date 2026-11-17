package com.group29.eventapp.presentation.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.group29.eventapp.R
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.databinding.DialogBottomFilterBinding

class FilterBottomSheetFragment(
    private var currentCategory: String? = null,
    private var currentStatus: EventStatus? = null,
    private var currentIsOnline: Boolean? = null,
    private val onApplyFilters: (category: String?, status: EventStatus?, isOnline: Boolean?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogBottomFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogBottomFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Phục hồi lựa chọn trước đó nếu có
        setupInitialSelections()

        binding.btnApplyFilter.setOnClickListener {
            val selectedCatChipId = binding.chipGroupFilterCategory.checkedChipId
            val category = if (selectedCatChipId != View.NO_ID && selectedCatChipId != R.id.chipFilterAllCat) {
                binding.chipGroupFilterCategory.findViewById<Chip>(selectedCatChipId)?.text?.toString()
            } else null

            val selectedOnlineChipId = binding.chipGroupFilterOnline.checkedChipId
            val isOnline = when (selectedOnlineChipId) {
                R.id.chipOnlineTrue -> true
                R.id.chipOnlineOffline -> false
                else -> null
            }

            val selectedStatusChipId = binding.chipGroupFilterStatus.checkedChipId
            val status = when (selectedStatusChipId) {
                R.id.chipStatusPublished -> EventStatus.Published
                R.id.chipStatusOngoing -> EventStatus.Ongoing
                R.id.chipStatusCompleted -> EventStatus.Completed
                else -> null
            }

            onApplyFilters(category, status, isOnline)
            dismiss()
        }

        binding.btnResetFilter.setOnClickListener {
            binding.chipFilterAllCat.isChecked = true
            binding.chipOnlineAll.isChecked = true
            binding.chipStatusAll.isChecked = true
            onApplyFilters(null, null, null)
            dismiss()
        }
    }

    private fun setupInitialSelections() {
        if (currentCategory != null) {
            for (i in 0 until binding.chipGroupFilterCategory.childCount) {
                val chip = binding.chipGroupFilterCategory.getChildAt(i) as? Chip
                if (chip?.text?.toString() == currentCategory) {
                    chip?.isChecked = true
                    break
                }
            }
        }

        when (currentIsOnline) {
            true -> binding.chipOnlineTrue.isChecked = true
            false -> binding.chipOnlineOffline.isChecked = true
            null -> binding.chipOnlineAll.isChecked = true
        }

        when (currentStatus) {
            EventStatus.Published -> binding.chipStatusPublished.isChecked = true
            EventStatus.Ongoing -> binding.chipStatusOngoing.isChecked = true
            EventStatus.Completed -> binding.chipStatusCompleted.isChecked = true
            else -> binding.chipStatusAll.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
