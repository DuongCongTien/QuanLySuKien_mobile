package com.group29.eventapp.presentation.events.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.EventSessionModel
import com.group29.eventapp.databinding.ItemEventSessionBinding

class EventSessionAdapter(
    private val sessions: List<EventSessionModel>
) : RecyclerView.Adapter<EventSessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(val binding: ItemEventSessionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemEventSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        with(holder.binding) {
            tvSessionTitle.text = session.title
            tvSessionSpeaker.text = "Diễn giả: ${session.speaker}"
            tvSessionRoom.text = "Phòng: ${session.room}"
            tvSessionTime.text = "${DateTimeUtils.formatTimeOnly(session.startTime)} - ${DateTimeUtils.formatTimeOnly(session.endTime)}"
            tvSessionTag.visibility = if (session.isMandatory) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int = sessions.size
}
