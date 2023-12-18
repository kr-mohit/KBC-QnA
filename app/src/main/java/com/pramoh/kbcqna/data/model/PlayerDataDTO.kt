package com.pramoh.kbcqna.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pramoh.kbcqna.domain.model.PlayerData

@Entity(tableName = "leaderboard_player")
data class PlayerDataDTO(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val playerName: String?,
    val moneyWon: Int?
)

fun PlayerDataDTO.toDomainPlayerData(): PlayerData {
    return PlayerData(
        id = this.id,
        playerName = this.playerName ?: "",
        moneyWon = this.moneyWon ?: 0
    )
}
