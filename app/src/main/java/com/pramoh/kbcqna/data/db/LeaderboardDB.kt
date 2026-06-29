package com.pramoh.kbcqna.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pramoh.kbcqna.data.model.QuestionEntity
import com.pramoh.kbcqna.domain.model.PlayerData

@Database(
    entities = [PlayerData::class, QuestionEntity::class],
    version = 2
)
abstract class LeaderboardDB: RoomDatabase() {

    abstract fun getLeaderboardDAO(): LeaderboardDAO

    abstract fun getQuestionDAO(): QuestionDAO
}