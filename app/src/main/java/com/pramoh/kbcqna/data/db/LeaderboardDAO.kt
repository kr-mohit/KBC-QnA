package com.pramoh.kbcqna.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pramoh.kbcqna.data.model.PlayerDataDTO

@Dao
interface LeaderboardDAO {

    @Insert
    suspend fun insertPlayer(player: PlayerDataDTO)

    @Query("SELECT * FROM leaderboard_player ORDER BY moneyWon DESC LIMIT 10")
    suspend fun getTopPlayers() : List<PlayerDataDTO>

    @Query("DELETE FROM leaderboard_player")
    suspend fun deleteAllPlayers()

    @Delete
    suspend fun deletePlayer(player: PlayerDataDTO)
}