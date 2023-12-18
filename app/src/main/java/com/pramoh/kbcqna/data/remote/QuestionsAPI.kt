package com.pramoh.kbcqna.data.remote

import com.pramoh.kbcqna.data.model.QuestionDTO
import retrofit2.http.GET
import retrofit2.http.Url

interface QuestionsAPI {

    @GET
    suspend fun getQuestionsFromUrl(@Url url: String): List<QuestionDTO>
}