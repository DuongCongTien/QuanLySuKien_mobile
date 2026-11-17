package com.group29.eventapp.presentation.secretmission.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.MissionAssignmentStatus
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.databinding.ItemSecretMissionCardBinding

class SecretMissionAdapter(
    private val onMissionClick: (SecretMissionModel) -> Unit
) : ListAdapter<SecretMissionModel, SecretMissionAdapter.MissionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val binding = ItemSecretMissionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MissionViewHolder(private val binding: ItemSecretMissionCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SecretMissionModel) {
            val context = binding.root.context

            if (item.assignmentStatus == MissionAssignmentStatus.Locked) {
                // Masked content for locked missions
                binding.tvMissionTitle.text = context.getString(R.string.mission_masked_title)
                binding.tvMissionDesc.text = context.getString(R.string.mission_masked_desc)
                binding.chipReward.text = "??? EXP"
                binding.chipStatus.text = "Bị khóa"
                binding.chipStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
            } else {
                binding.tvMissionTitle.text = item.title
                binding.tvMissionDesc.text = item.description
                binding.chipReward.text = "+${item.rewardPoints} EXP"
                binding.chipStatus.text = item.assignmentStatus.displayName

                when (item.assignmentStatus) {
                    MissionAssignmentStatus.Available -> {
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_secondary_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                    }
                    MissionAssignmentStatus.InProgress -> {
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_warning_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                    }
                    MissionAssignmentStatus.PendingVerification -> {
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_secondary_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                    }
                    MissionAssignmentStatus.Completed -> {
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_success_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                    }
                    MissionAssignmentStatus.Rejected,
                    MissionAssignmentStatus.Expired -> {
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_error_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_error_container))
                    }
                    else -> {}
                }
            }

            val remaining = item.expiresAt - System.currentTimeMillis()
            if (remaining > 0) {
                binding.tvCountdown.text = "Hết hạn sau: ${DateTimeUtils.formatCountdown(remaining)}"
            } else {
                binding.tvCountdown.text = "Đã hết hạn"
            }

            binding.root.setOnClickListener { onMissionClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SecretMissionModel>() {
            override fun areItemsTheSame(oldItem: SecretMissionModel, newItem: SecretMissionModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SecretMissionModel, newItem: SecretMissionModel): Boolean =
                oldItem == newItem
        }
    }
}
