package com.pramoh.kbcqna.presentation.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.databinding.ItemAdminStatBinding
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import androidx.core.graphics.toColorInt

class AdminStatsAdapter(
    private val statsList: List<Pair<Int, Int>>
): RecyclerView.Adapter<AdminStatsAdapter.StatViewHolder>() {

    class StatViewHolder(val binding: ItemAdminStatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val binding = ItemAdminStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        val (prize, count) = statsList[position]
        val formattedAmount = MoneyTypeConversionUtil.convertToString(prize)

        holder.binding.tvPrizeLevel.text = formattedAmount
        holder.binding.tvCount.text = "$count questions"

        if (count < 5) {
            // Warn style in red
            holder.binding.tvCount.setTextColor("#FF4444".toColorInt())
        } else {
            holder.binding.tvCount.setTextColor("#E0E0E0".toColorInt())
        }
    }

    override fun getItemCount(): Int = statsList.size
}
