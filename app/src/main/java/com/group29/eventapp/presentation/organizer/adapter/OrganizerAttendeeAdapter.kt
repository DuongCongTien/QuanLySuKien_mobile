package com.group29.eventapp.presentation.organizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.data.model.CheckInStatus
import com.group29.eventapp.data.model.RegistrationModel
import com.group29.eventapp.data.model.RegistrationStatus
import com.group29.eventapp.databinding.ItemOrganizerAttendeeBinding

class OrganizerAttendeeAdapter(
    private val onManualCheckInClick: (RegistrationModel) -> Unit
) : ListAdapter<RegistrationModel, OrganizerAttendeeAdapter.AttendeeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val binding = ItemOrganizerAttendeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AttendeeViewHolder(private val binding: ItemOrganizerAttendeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RegistrationModel) {
            val context = binding.root.context
            binding.tvAttendeeName.text = item.userName
            binding.tvAttendeeEmail.text = item.userEmail
            binding.tvTicketCode.text = "Mã vé: ${item.ticketCode}"

            when {
                item.status == RegistrationStatus.Cancelled -> {
                    binding.chipCheckInStatus.text = context.getString(R.string.status_ticket_cancelled)
                    binding.chipCheckInStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                    binding.chipCheckInStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                    binding.btnManualCheckIn.visibility = View.GONE
                }
                item.checkInStatus == CheckInStatus.CheckedIn -> {
                    binding.chipCheckInStatus.text = context.getString(R.string.status_checkin_done)
                    binding.chipCheckInStatus.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipCheckInStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                    binding.btnManualCheckIn.visibility = View.GONE
                }
                else -> {
                    binding.chipCheckInStatus.text = context.getString(R.string.status_checkin_not)
                    binding.chipCheckInStatus.setChipBackgroundColorResource(R.color.color_warning_container)
                    binding.chipCheckInStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                    binding.btnManualCheckIn.visibility = View.VISIBLE
                    binding.btnManualCheckIn.setOnClickListener { onManualCheckInClick(item) }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RegistrationModel>() {
            override fun areItemsTheSame(oldItem: RegistrationModel, newItem: RegistrationModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RegistrationModel, newItem: RegistrationModel): Boolean =
                oldItem == newItem
        }
    }
}
