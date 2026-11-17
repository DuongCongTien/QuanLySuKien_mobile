package com.group29.eventapp.presentation.timecapsule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.R
import com.group29.eventapp.common.DateTimeUtils
import com.group29.eventapp.data.model.TimeCapsuleModel
import com.group29.eventapp.data.model.TimeCapsuleStatus
import com.group29.eventapp.databinding.ItemCapsuleCardBinding

class CapsuleAdapter(
    private val onCapsuleClick: (TimeCapsuleModel) -> Unit
) : ListAdapter<TimeCapsuleModel, CapsuleAdapter.CapsuleViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CapsuleViewHolder {
        val binding = ItemCapsuleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CapsuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CapsuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CapsuleViewHolder(private val binding: ItemCapsuleCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeCapsuleModel) {
            val context = binding.root.context
            val isLocked = item.status == TimeCapsuleStatus.Locked || item.status == TimeCapsuleStatus.Sealed

            binding.tvCapsuleTitle.text = item.title
            binding.chipType.text = item.type.displayName
            binding.tvRecipient.text = "Gửi tới: ${item.recipientName}"

            if (isLocked) {
                binding.ivLockIcon.visibility = View.VISIBLE
                binding.tvCapsuleMessage.text = "Thông điệp đang được niêm phong an toàn. Nội dung sẽ hiển thị sau khi mở khóa."
                binding.tvCapsuleMessage.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                binding.chipStatus.text = "Đang khóa"
                binding.chipStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
            } else {
                binding.ivLockIcon.visibility = View.GONE
                binding.tvCapsuleMessage.text = item.message
                binding.tvCapsuleMessage.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface))

                when (item.status) {
                    TimeCapsuleStatus.AvailableToOpen -> {
                        binding.chipStatus.text = "Sẵn sàng mở"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_success_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_success_container))
                    }
                    TimeCapsuleStatus.Opened -> {
                        binding.chipStatus.text = "Đã mở"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_secondary_container)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_secondary_container))
                    }
                    TimeCapsuleStatus.Archived -> {
                        binding.chipStatus.text = "Lưu trữ"
                        binding.chipStatus.setChipBackgroundColorResource(R.color.color_surface_variant)
                        binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.color_on_surface_variant))
                    }
                    else -> {}
                }
            }

            val remaining = item.unlockAt - System.currentTimeMillis()
            if (remaining > 0) {
                binding.tvCountdown.text = "Mở khóa sau: ${DateTimeUtils.formatCountdown(remaining)}"
            } else {
                binding.tvCountdown.text = "Đã tới hạn mở khóa"
            }

            binding.root.setOnClickListener { onCapsuleClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimeCapsuleModel>() {
            override fun areItemsTheSame(oldItem: TimeCapsuleModel, newItem: TimeCapsuleModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TimeCapsuleModel, newItem: TimeCapsuleModel): Boolean =
                oldItem == newItem
        }
    }
}
