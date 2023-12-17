package com.pramoh.kbcqna.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pramoh.kbcqna.domain.model.OfflineQuestion

@Database(entities = [OfflineQuestion::class], version = 1)
abstract class OfflineQuestionsDB: RoomDatabase() {

    abstract fun getOfflineQuestionsDAO(): OfflineQuestionsDAO
}