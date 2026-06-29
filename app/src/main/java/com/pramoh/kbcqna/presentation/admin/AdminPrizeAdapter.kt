package com.pramoh.kbcqna.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.databinding.ItemAdminPrizeBinding
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil

class AdminPrizeAdapter(
    private val options: List<AdminPrizeOption>
): RecyclerView.Adapter<AdminPrizeAdapter.PrizeViewHolder>() {

    class PrizeViewHolder(val binding: ItemAdminPrizeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val binding = ItemAdminPrizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        val option = options[position]
        val formattedAmount = MoneyTypeConversionUtil.convertToString(option.amount)
        holder.binding.cbPrize.text = formattedAmount

        holder.binding.cbPrize.setOnCheckedChangeListener(null)
        holder.binding.cbPrize.isChecked = option.isSelected

        holder.binding.cbPrize.setOnCheckedChangeListener { _, isChecked ->
            option.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = options.size

    fun getSelectedPrizes(): List<Int> {
        return options.filter { it.isSelected }.map { it.amount }.sorted()
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
