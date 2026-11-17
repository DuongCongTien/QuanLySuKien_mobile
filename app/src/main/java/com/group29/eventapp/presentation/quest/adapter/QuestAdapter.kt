package com.group29.eventapp.presentation.quest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.data.model.QuestCompletionStatus
import com.group29.eventapp.data.model.QuestModel
import com.group29.eventapp.databinding.ItemQuestCardBinding

class QuestAdapter(
    private val onActionClick: (QuestModel) -> Unit
) : ListAdapter<QuestModel, QuestAdapter.QuestViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val binding = ItemQuestCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemQuestCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestModel) {
            val context = binding.root.context
            binding.tvQuestTitle.text = item.title
            binding.tvQuestDesc.text = item.description
            binding.chipPoints.text = "+${item.points} EXP"

            when (item.completionStatus) {
                QuestCompletionStatus.Completed -> {
                    binding.chipStatus.text = "Hoàn thành"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_success_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                    binding.btnDoQuest.visibility = View.GONE
                }
                QuestCompletionStatus.PendingVerification -> {
                    binding.chipStatus.text = "Chờ xác minh"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_secondary_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                    binding.btnDoQuest.visibility = View.GONE
                }
                QuestCompletionStatus.InProgress -> {
                    binding.chipStatus.text = "Đang thực hiện"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_warning_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_warning_container))
                    binding.btnDoQuest.visibility = View.VISIBLE
                    binding.btnDoQuest.text = "Gửi xác minh"
                    binding.btnDoQuest.setOnClickListener { onActionClick(item) }
                }
                QuestCompletionStatus.Rejected -> {
                    binding.chipStatus.text = "Bị từ chối"
                    binding.chipStatus.setChipBackgroundColorResource(R.color.color_error_container)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_error_container))
                    binding.btnDoQuest.visibility = View.VISIBLE
                    binding.btnDoQuest.text = "Thử lại"
                    binding.btnDoQuest.setOnClickListener { onActionClick(item) }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestModel>() {
            override fun areItemsTheSame(oldItem: QuestModel, newItem: QuestModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: QuestModel, newItem: QuestModel): Boolean =
                oldItem == newItem
        }
    }
}
