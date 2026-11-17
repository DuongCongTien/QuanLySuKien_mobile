package com.group29.eventapp.presentation.waitlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.WaitlistEntryModel
import com.group29.eventapp.data.model.WaitlistEntryStatus
import com.group29.eventapp.databinding.ItemWaitlistEntryBinding

class OrganizerWaitlistAdapter : ListAdapter<WaitlistEntryModel, OrganizerWaitlistAdapter.EntryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemWaitlistEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EntryViewHolder(private val binding: ItemWaitlistEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WaitlistEntryModel) {
            val context = binding.root.context
            binding.tvQueueBadge.text = "#${item.queuePosition}"
            binding.tvUserName.text = item.userName
            binding.tvUserEmail.text = item.userEmail
            binding.tvJoinedTime.text = "Gia nhập: ${DateTimeUtils.formatDateTime(item.joinedAt)}"

            binding.chipStatus.text = item.status.displayName
            when (item.status) {
                WaitlistEntryStatus.Offered -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_warning_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                }
                WaitlistEntryStatus.Registered -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                }
                WaitlistEntryStatus.Waiting -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_primary))
                }
                else -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WaitlistEntryModel>() {
            override fun areItemsTheSame(oldItem: WaitlistEntryModel, newItem: WaitlistEntryModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WaitlistEntryModel, newItem: WaitlistEntryModel): Boolean =
                oldItem == newItem
        }
    }
}
