package com.pramoh.kbcqna.data.db

import androidx.room.Dao
import androidx.room.Query
import com.pramoh.kbcqna.data.model.QuestionDTO

@Dao
interface OfflineQuestionsDAO {

    @Query("SELECT * FROM offline_question WHERE question_level_id = :level")
    suspend fun getQuestionsForLevel(level: Int) : List<QuestionDTO>

}