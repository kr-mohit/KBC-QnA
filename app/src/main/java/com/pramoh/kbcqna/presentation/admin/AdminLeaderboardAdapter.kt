package com.pramoh.kbcqna.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.databinding.ItemAdminLeaderboardPlayerBinding
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil

class AdminLeaderboardAdapter(
    private val options: List<AdminLeaderboardOption>
): RecyclerView.Adapter<AdminLeaderboardAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(val binding: ItemAdminLeaderboardPlayerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemAdminLeaderboardPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val option = options[position]
        val formattedAmount = MoneyTypeConversionUtil.convertToString(option.moneyWon)
        holder.binding.cbPlayer.text = "${option.playerName} - $formattedAmount"

        holder.binding.cbPlayer.setOnCheckedChangeListener(null)
        holder.binding.cbPlayer.isChecked = option.isSelected

        holder.binding.cbPlayer.setOnCheckedChangeListener { _, isChecked ->
            option.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = options.size

    fun getSelectedDocIds(): List<String> {
        return options.filter { it.isSelected }.map { it.docId }
    }

    fun selectAll() {
        options.forEach { it.isSelected = true }
        notifyDataSetChanged()
    }

    fun unselectAll() {
        options.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }
}
