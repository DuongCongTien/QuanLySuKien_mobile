package com.group29.eventapp.presentation.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.EventStatus
import com.group29.eventapp.databinding.ItemEventCardSmallBinding

class RecommendedEventAdapter(
    private var events: List<EventModel>,
    private val onEventClick: (EventModel) -> Unit
) : RecyclerView.Adapter<RecommendedEventAdapter.SmallViewHolder>() {

    class SmallViewHolder(val binding: ItemEventCardSmallBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newEvents: List<EventModel>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallViewHolder {
        val binding = ItemEventCardSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SmallViewHolder, position: Int) {
        val event = events[position]
        with(holder.binding) {
            tvSmallCardTitle.text = event.title
            tvSmallCardTime.text = DateTimeUtils.formatDateTime(event.startTime)
            tvSmallCardStatus.text = event.status.displayName

            when (event.status) {
                EventStatus.Ongoing -> {
                    tvSmallCardStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                    tvSmallCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_info))
                }
                EventStatus.Published -> {
                    tvSmallCardStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                    tvSmallCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_success))
                }
                else -> {
                    tvSmallCardStatus.setBackgroundResource(R.drawable.bg_chip_status_neutral)
                    tvSmallCardStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_neutral))
                }
            }

            root.setOnClickListener {
                onEventClick(event)
            }
        }
    }

    override fun getItemCount(): Int = events.size
}
