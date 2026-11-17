package com.group29.eventapp.presentation.quest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group29.eventapp.databinding.ItemLeaderboardUserBinding

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val title: String,
    val points: Int
)

class LeaderboardAdapter(
    private val items: List<LeaderboardUser>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderboardUser) {
            binding.tvRank.text = item.rank.toString()
            binding.tvUserName.text = item.name
            binding.tvUserTitle.text = item.title
            binding.tvPoints.text = "${item.points} EXP"
        }
    }
}
