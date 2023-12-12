package com.pramoh.kbcqna.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pramoh.kbcqna.domain.model.PlayerData

@Dao
interface LeaderboardDAO {

    @Insert
    suspend fun insertPlayer(player: PlayerData)

    @Query("SELECT * FROM PlayerData ORDER BY moneyWon DESC LIMIT 10")
    suspend fun getTopPlayers() : List<PlayerData>

    @Query("DELETE FROM PlayerData")
    suspend fun deleteAllPlayers()

    @Delete
    suspend fun deletePlayer(player: PlayerData)
}