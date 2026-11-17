package com.group29.eventapp.presentation.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.databinding.ItemEventFeaturedBinding

class FeaturedEventAdapter(
    private var events: List<EventModel>,
    private val onEventClick: (EventModel) -> Unit
) : RecyclerView.Adapter<FeaturedEventAdapter.FeaturedViewHolder>() {

    class FeaturedViewHolder(val binding: ItemEventFeaturedBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newEvents: List<EventModel>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedViewHolder {
        val binding = ItemEventFeaturedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeaturedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedViewHolder, position: Int) {
        val event = events[position]
        with(holder.binding) {
            tvFeaturedCategory.text = "${event.category} • Nổi bật"
            tvFeaturedTitle.text = event.title
            tvFeaturedTime.text = "${DateTimeUtils.formatDateTime(event.startTime)} - ${DateTimeUtils.formatTimeOnly(event.endTime)}"
            tvFeaturedLocation.text = if (event.isOnline) "Trực tuyến" else event.location
            root.setOnClickListener {
                onEventClick(event)
            }
        }
    }

    override fun getItemCount(): Int = events.size
}
