package com.group29.eventapp.presentation.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.data.model.EventModel
import com.group29.eventapp.data.model.RegistrationModel
import com.group29.eventapp.data.model.RegistrationStatus
import com.group29.eventapp.databinding.ItemMyEventCardBinding

data class MyEventItem(
    val registration: RegistrationModel,
    val event: EventModel
)

class MyEventItemAdapter(
    private var items: List<MyEventItem>,
    private val onViewTicket: (MyEventItem) -> Unit,
    private val onViewEvent: (MyEventItem) -> Unit
) : RecyclerView.Adapter<MyEventItemAdapter.MyEventViewHolder>() {

    class MyEventViewHolder(val binding: ItemMyEventCardBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<MyEventItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventViewHolder {
        val binding = ItemMyEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyEventViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMyEventTitle.text = item.event.title
            tvMyEventTime.text = "${DateTimeUtils.formatDateTime(item.event.startTime)}"
            tvMyEventLocation.text = if (item.event.isOnline) "Trực tuyến" else item.event.location
            tvMyEventTicketCode.text = "Mã vé: ${item.registration.ticketCode}"

            // Status chip
            when {
                item.registration.status == RegistrationStatus.Cancelled -> {
                    tvMyEventStatus.text = "Đã hủy vé"
                    tvMyEventStatus.setBackgroundResource(R.drawable.bg_chip_status_error)
                    tvMyEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_error))
                    btnViewTicketAction.text = "Xem chi tiết"
                    btnViewTicketAction.setIconResource(R.drawable.ic_nav_events)
                }
                item.registration.checkInStatus == CheckInStatus.CheckedIn -> {
                    tvMyEventStatus.text = "Đã check-in"
                    tvMyEventStatus.setBackgroundResource(R.drawable.bg_chip_status_success)
                    tvMyEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_success))
                    btnViewTicketAction.text = "Xem vé QR"
                    btnViewTicketAction.setIconResource(R.drawable.ic_nav_scan)
                }
                else -> {
                    tvMyEventStatus.text = "Chưa check-in"
                    tvMyEventStatus.setBackgroundResource(R.drawable.bg_chip_status_info)
                    tvMyEventStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_info))
                    btnViewTicketAction.text = "Xem vé QR"
                    btnViewTicketAction.setIconResource(R.drawable.ic_nav_scan)
                }
            }

            btnViewTicketAction.setOnClickListener {
                if (item.registration.status == RegistrationStatus.Cancelled) {
                    onViewEvent(item)
                } else {
                    onViewTicket(item)
                }
            }

            root.setOnClickListener {
                onViewEvent(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
