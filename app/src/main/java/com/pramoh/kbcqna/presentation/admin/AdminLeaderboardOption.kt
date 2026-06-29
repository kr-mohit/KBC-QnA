package com.pramoh.kbcqna.presentation.admin

data class AdminLeaderboardOption(
    val docId: String,
    val playerName: String,
    val moneyWon: Int,
    var isSelected: Boolean = false
)
