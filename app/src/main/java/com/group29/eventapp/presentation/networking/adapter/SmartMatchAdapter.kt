package com.group29.eventapp.presentation.networking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.data.model.ConnectionStatus
import com.group29.eventapp.data.model.NetworkingMatchModel
import com.group29.eventapp.databinding.ItemInvitationCardBinding
import com.group29.eventapp.databinding.ItemMatchUserCardBinding

class SmartMatchAdapter(
    private val onSendInviteClick: (NetworkingMatchModel) -> Unit
) : ListAdapter<NetworkingMatchModel, SmartMatchAdapter.MatchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchUserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MatchViewHolder(private val binding: ItemMatchUserCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NetworkingMatchModel) {
            val user = item.user
            binding.tvUserName.text = user.name
            binding.chipScore.text = "${item.matchScore}% Phù hợp"
            binding.tvUserHeadline.text = "${user.title} @ ${user.company}"
            binding.tvMatchReason.text = "Lý do: ${item.matchReason}"
            binding.tvBio.text = user.bio

            if (item.connectionStatus == ConnectionStatus.Pending) {
                binding.btnConnect.isEnabled = false
                binding.btnConnect.text = binding.root.context.getString(R.string.invitation_sent)
            } else if (item.connectionStatus == ConnectionStatus.Accepted) {
                binding.btnConnect.isEnabled = false
                binding.btnConnect.text = "Đã kết nối"
            } else {
                binding.btnConnect.isEnabled = true
                binding.btnConnect.text = binding.root.context.getString(R.string.btn_send_invitation)
                binding.btnConnect.setOnClickListener { onSendInviteClick(item) }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NetworkingMatchModel>() {
            override fun areItemsTheSame(oldItem: NetworkingMatchModel, newItem: NetworkingMatchModel): Boolean =
                oldItem.user.id == newItem.user.id

            override fun areContentsTheSame(oldItem: NetworkingMatchModel, newItem: NetworkingMatchModel): Boolean =
                oldItem == newItem
        }
    }
}

class InvitationAdapter(
    private val onAcceptClick: (NetworkingMatchModel) -> Unit,
    private val onDeclineClick: (NetworkingMatchModel) -> Unit
) : ListAdapter<NetworkingMatchModel, InvitationAdapter.InvitationViewHolder>(SmartMatchAdapter.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val binding = ItemInvitationCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvitationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InvitationViewHolder(private val binding: ItemInvitationCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NetworkingMatchModel) {
            val user = item.user
            binding.tvUserName.text = user.name
            binding.chipScore.text = "${item.matchScore}% Phù hợp"
            binding.tvUserHeadline.text = "${user.title} @ ${user.company}"
            binding.tvMatchReason.text = "Lời mời kết nối: ${item.matchReason}"

            binding.btnAccept.setOnClickListener { onAcceptClick(item) }
            binding.btnDecline.setOnClickListener { onDeclineClick(item) }
        }
    }
}
