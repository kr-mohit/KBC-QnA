package com.pramoh.kbcqna.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.domain.model.LeaderboardData

class LeaderboardAdapter(private val list: List<LeaderboardData>)
    : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_score, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.tvPlayerName.text = list[position].playerName
        holder.tvMoney.text = list[position].moneyWon.toString()
    }

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlayerName: TextView = itemView.findViewById(R.id.tv_player_name)
        val tvMoney: TextView = itemView.findViewById(R.id.tv_money)
    }
}