package com.pramoh.kbcqna.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.R

class PrizeListAdapter(private val list: List<String>, private val currentQuestion: Int)
    : RecyclerView.Adapter<PrizeListAdapter.PrizeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prize_list, parent, false)
        return PrizeListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PrizeListViewHolder, position: Int) {
        holder.tvPrizeAmount.text = list[position]
        if (position == (list.size - currentQuestion))
            holder.tvPrizeAmount.setBackgroundColor(Color.GREEN)
        else
            holder.tvPrizeAmount.setBackgroundColor(Color.YELLOW)
    }

    class PrizeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrizeAmount = itemView.findViewById<TextView>(R.id.tv_prize_amount)
    }
}