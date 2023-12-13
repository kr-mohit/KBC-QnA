package com.pramoh.kbcqna.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val playerName: String,
    val moneyWon: Int
)
