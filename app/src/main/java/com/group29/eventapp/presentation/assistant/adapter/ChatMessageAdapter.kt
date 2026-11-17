package com.group29.eventapp.presentation.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.ChatMessageModel
import com.group29.eventapp.databinding.ItemChatMessageBinding

class ChatMessageAdapter : ListAdapter<ChatMessageModel, ChatMessageAdapter.ChatViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatMessageModel) {
            val formattedTime = DateTimeUtils.formatTimeOnly(item.timestamp)

            if (item.isFromAi) {
                binding.layoutAiMessage.visibility = View.VISIBLE
                binding.layoutUserMessage.visibility = View.GONE
                binding.tvAiSender.text = item.sender
                binding.tvAiText.text = item.message
                binding.tvAiTime.text = formattedTime

                if (!item.sourceReference.isNullOrEmpty()) {
                    binding.tvSource.visibility = View.VISIBLE
                    binding.tvSource.text = "Nguồn tham chiếu: ${item.sourceReference}"
                } else {
                    binding.tvSource.visibility = View.GONE
                }
            } else {
                binding.layoutAiMessage.visibility = View.GONE
                binding.layoutUserMessage.visibility = View.VISIBLE
                binding.tvUserText.text = item.message
                binding.tvUserTime.text = formattedTime
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatMessageModel>() {
            override fun areItemsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean =
                oldItem == newItem
        }
    }
}
