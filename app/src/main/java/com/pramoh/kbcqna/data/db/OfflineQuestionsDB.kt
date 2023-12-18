package com.pramoh.kbcqna.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pramoh.kbcqna.data.model.QuestionDTO

@Database(entities = [QuestionDTO::class], version = 1)
abstract class OfflineQuestionsDB: RoomDatabase() {

    abstract fun getOfflineQuestionsDAO(): OfflineQuestionsDAO
}