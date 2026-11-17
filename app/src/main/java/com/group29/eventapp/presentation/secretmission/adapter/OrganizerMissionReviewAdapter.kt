package com.group29.eventapp.presentation.secretmission.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.data.model.MissionAssignmentStatus
import com.group29.eventapp.data.model.SecretMissionModel
import com.group29.eventapp.databinding.ItemMissionEvidenceReviewBinding

class OrganizerMissionReviewAdapter(
    private val onApproveClick: (SecretMissionModel) -> Unit,
    private val onRejectClick: (SecretMissionModel) -> Unit
) : ListAdapter<SecretMissionModel, OrganizerMissionReviewAdapter.ReviewViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemMissionEvidenceReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: ItemMissionEvidenceReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SecretMissionModel) {
            val context = binding.root.context
            binding.tvMissionTitle.text = item.title
            binding.chipStatus.text = item.assignmentStatus.displayName
            binding.tvEvidenceContent.text = item.evidenceSubmitted ?: "Chưa có bằng chứng"

            if (item.assignmentStatus == MissionAssignmentStatus.PendingVerification) {
                binding.layoutActionButtons.visibility = View.VISIBLE
                binding.chipStatus.setChipBackgroundColorResource(R.color.color_warning_container)
                binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                binding.btnApprove.setOnClickListener { onApproveClick(item) }
                binding.btnReject.setOnClickListener { onRejectClick(item) }
            } else {
                binding.layoutActionButtons.visibility = View.GONE
                if (item.assignmentStatus == MissionAssignmentStatus.Completed) {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                } else {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_error_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_error_container))
                }
            }
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
