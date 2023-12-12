package com.pramoh.kbcqna.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pramoh.kbcqna.domain.model.LeaderboardData

@Database(entities = [LeaderboardData::class], version = 1)
abstract class LeaderboardDB: RoomDatabase() {

    abstract fun getLeaderboardDAO(): LeaderboardDAO
}