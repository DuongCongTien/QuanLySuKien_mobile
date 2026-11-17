package com.group29.eventapp.presentation.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.NotificationModel
import com.group29.eventapp.data.model.NotificationType
import com.group29.eventapp.databinding.ItemNotificationCardBinding

class NotificationAdapter(
    private val onItemClick: (NotificationModel) -> Unit
) : ListAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationModel) {
            val context = binding.root.context
            binding.tvNotificationTitle.text = item.title
            binding.tvNotificationMessage.text = item.summary
            binding.tvTimestamp.text = DateTimeUtils.formatDateTime(item.timestamp)

            binding.viewUnreadDot.visibility = if (item.isRead) View.INVISIBLE else View.VISIBLE

            binding.chipType.text = item.type.displayName
            when (item.type) {
                NotificationType.Mission -> {
                    binding.chipType.setChipBackgroundColorResource(R.color.color_secondary_container)
                    binding.chipType.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                }
                NotificationType.SeatOffer -> {
                    binding.chipType.setChipBackgroundColorResource(R.color.color_warning_container)
                    binding.chipType.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                }
                NotificationType.TimeCapsule -> {
                    binding.chipType.setChipBackgroundColorResource(R.color.primary_light)
                    binding.chipType.setTextColor(ContextCompat.getColor(context, R.color.primary))
                }
                else -> {
                    binding.chipType.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipType.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                }
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificationModel>() {
            override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean =
                oldItem == newItem
        }
    }
}
