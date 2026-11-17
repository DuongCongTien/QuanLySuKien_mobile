package com.group29.eventapp.presentation.journey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.JourneyStepModel
import com.group29.eventapp.data.model.JourneyStepProgress
import com.group29.eventapp.databinding.ItemJourneyTimelineStepBinding

class JourneyStepAdapter(
    private val onStepClick: (JourneyStepModel) -> Unit
) : ListAdapter<JourneyStepModel, JourneyStepAdapter.StepViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemJourneyTimelineStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StepViewHolder(private val binding: ItemJourneyTimelineStepBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JourneyStepModel) {
            val context = binding.root.context
            val startTimeStr = DateTimeUtils.formatTimeOnly(item.startTime)
            val endTimeStr = DateTimeUtils.formatTimeOnly(item.endTime)
            binding.tvStepTime.text = "$startTimeStr - $endTimeStr (${item.durationMinutes} phút)"

            binding.tvStepTitle.text = item.title
            binding.tvStepLocation.text = "Địa điểm: ${item.location}"
            binding.tvStepReason.text = "Lý do đề xuất: ${item.recommendationReason}"
            binding.tvFitScore.text = "Phù hợp: ${item.fitScore}%"

            if (item.isMandatory) {
                binding.chipMandatory.text = context.getString(R.string.journey_step_mandatory)
                binding.chipMandatory.setChipBackgroundColorResource(R.color.color_warning_container)
                binding.chipMandatory.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
            } else {
                binding.chipMandatory.text = context.getString(R.string.journey_step_optional)
                binding.chipMandatory.setChipBackgroundColorResource(R.color.color_surface_variant)
                binding.chipMandatory.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
            }

            binding.chipProgress.text = item.progress.displayName
            when (item.progress) {
                JourneyStepProgress.Completed -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                }
                JourneyStepProgress.InProgress -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_primary)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_primary))
                }
                JourneyStepProgress.Planned -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_secondary_container)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                }
                JourneyStepProgress.Recommended -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_primary))
                }
                JourneyStepProgress.Skipped -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
                JourneyStepProgress.Locked -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
                JourneyStepProgress.Unavailable -> {
                    binding.chipProgress.setChipBackgroundColorResource(R.color.color_error_container)
                    binding.chipProgress.setTextColor(ContextCompat.getColor(context, R.color.color_on_error_container))
                }
            }

            binding.btnStepAction.setOnClickListener { onStepClick(item) }
            binding.root.setOnClickListener { onStepClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<JourneyStepModel>() {
            override fun areItemsTheSame(oldItem: JourneyStepModel, newItem: JourneyStepModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: JourneyStepModel, newItem: JourneyStepModel): Boolean =
                oldItem == newItem
        }
    }
}
