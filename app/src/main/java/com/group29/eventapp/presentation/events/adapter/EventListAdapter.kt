package com.group29.eventapp.presentation.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.databinding.ItemEventCardBinding

class EventListAdapter(
    private val onEventClick: (EventModel) -> Unit
) : ListAdapter<EventModel, EventListAdapter.EventViewHolder>(DiffCallback) {

    class EventViewHolder(val binding: ItemEventCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        with(holder.binding) {
            tvCardTitle.text = event.title
            tvCardDesc.text = event.description
            tvCardCategory.text = event.category
            tvCardLocation.text = if (event.isOnline) "Trực tuyến" else event.location
            tvCardTime.text = "${DateTimeUtils.formatDateTime(event.startTime)} - ${DateTimeUtils.formatTimeOnly(event.endTime)}"

            val remainingSpots = maxOf(0, event.capacity - event.registeredCount)
            if (event.status == EventStatus.RegistrationClosed || remainingSpots == 0) {
                tvCardCapacity.text = "Đã hết chỗ (0 / ${event.capacity})"
                tvCardCapacity.setTextColor(ContextCompat.getColor(root.context, R.color.status_error))
            } else {
                tvCardCapacity.text = "Còn $remainingSpots chỗ trống / ${event.capacity}"
                tvCardCapacity.setTextColor(ContextCompat.getColor(root.context, R.color.primary))
            }

            // Status chip
            tvCardStatus.text = event.status.displayName
            when (event.status) {
                EventStatus.Published -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_success))
                }
                EventStatus.Ongoing -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_info))
                }
                EventStatus.RegistrationClosed -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_warning)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_warning))
                }
                EventStatus.Completed -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_neutral))
                }
                EventStatus.Cancelled -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_error)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_error))
                }
                EventStatus.Draft -> {
                    tvCardStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_locked))
                }
            }

            root.setOnClickListener {
                onEventClick(event)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EventModel>() {
        override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean = oldItem == newItem
    }
}
