package com.group29.eventapp.presentation.organizer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.databinding.ItemOrganizerEventCardBinding

class OrganizerEventAdapter(
    private var events: List<EventModel>,
    private val onViewAttendees: (EventModel) -> Unit,
    private val onEditEvent: (EventModel) -> Unit
) : RecyclerView.Adapter<OrganizerEventAdapter.OrgEventViewHolder>() {

    class OrgEventViewHolder(val binding: ItemOrganizerEventCardBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newEvents: List<EventModel>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgEventViewHolder {
        val binding = ItemOrganizerEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrgEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrgEventViewHolder, position: Int) {
        val event = events[position]
        with(holder.binding) {
            tvOrgEventTitle.text = event.title
            tvOrgEventTime.text = "${DateTimeUtils.formatDateTime(event.startTime)} - ${DateTimeUtils.formatTimeOnly(event.endTime)}"
            tvOrgEventLocation.text = if (event.isOnline) "Trực tuyến" else event.location
            tvOrgEventCapacity.text = "${event.registeredCount} / ${event.capacity} người"

            tvOrgEventStatus.text = event.status.displayName
            when (event.status) {
                EventStatus.Published -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_success))
                }
                EventStatus.Ongoing -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_info))
                }
                EventStatus.RegistrationClosed -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_warning)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_warning))
                }
                EventStatus.Completed -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_neutral))
                }
                EventStatus.Cancelled -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_error)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_error))
                }
                EventStatus.Draft -> {
                    tvOrgEventStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvOrgEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_locked))
                }
            }

            btnViewAttendees.setOnClickListener {
                onViewAttendees(event)
            }

            btnEditEvent.setOnClickListener {
                onEditEvent(event)
            }
        }
    }

    override fun getItemCount(): Int = events.size
}
