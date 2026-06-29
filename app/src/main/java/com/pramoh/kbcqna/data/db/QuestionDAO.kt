package com.pramoh.kbcqna.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pramoh.kbcqna.data.model.QuestionEntity

@Dao
interface QuestionDAO {

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Insert
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>
}