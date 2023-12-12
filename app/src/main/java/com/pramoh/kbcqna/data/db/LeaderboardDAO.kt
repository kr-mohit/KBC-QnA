package com.pramoh.kbcqna.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pramoh.kbcqna.domain.model.LeaderboardData

@Dao
interface LeaderboardDAO {

    @Insert
    suspend fun addScore(score: LeaderboardData)

    @Query("SELECT * FROM LeaderboardData")
    suspend fun getScores() : List<LeaderboardData>
}