package com.group29.eventapp.presentation.organizer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.data.model.QuestModel
import com.group29.eventapp.data.model.QuestStatus
import com.group29.eventapp.databinding.ItemOrganizerQuestBinding

class OrganizerQuestAdapter(
    private val onQuestClick: (QuestModel) -> Unit
) : ListAdapter<QuestModel, OrganizerQuestAdapter.QuestViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val binding = ItemOrganizerQuestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemOrganizerQuestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestModel) {
            val context = binding.root.context
            binding.tvQuestTitle.text = item.title
            binding.tvQuestDesc.text = item.description
            binding.chipQuestPoints.text = "+${item.points} EXP"
            binding.tvQuestVerification.text = "Xác minh: ${item.verificationMethod} (Tối đa ${item.maxAttempts} lần)"

            when (item.status) {
                QuestStatus.Active -> {
                    binding.chipQuestStatus.text = "Đang hoạt động"
                    binding.chipQuestStatus.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipQuestStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                }
                QuestStatus.Draft -> {
                    binding.chipQuestStatus.text = "Bản nháp"
                    binding.chipQuestStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipQuestStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
                QuestStatus.Published -> {
                    binding.chipQuestStatus.text = "Đã công bố"
                    binding.chipQuestStatus.setChipBackgroundColorResource(R.color.color_secondary_container)
                    binding.chipQuestStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                }
                QuestStatus.Expired -> {
                    binding.chipQuestStatus.text = "Hết hạn"
                    binding.chipQuestStatus.setChipBackgroundColorResource(R.color.color_error_container)
                    binding.chipQuestStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_error_container))
                }
                QuestStatus.Disabled -> {
                    binding.chipQuestStatus.text = "Tạm dừng"
                    binding.chipQuestStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipQuestStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
            }

            binding.root.setOnClickListener { onQuestClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestModel>() {
            override fun areItemsTheSame(oldItem: QuestModel, newItem: QuestModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: QuestModel, newItem: QuestModel): Boolean =
                oldItem == newItem
        }
    }
}
