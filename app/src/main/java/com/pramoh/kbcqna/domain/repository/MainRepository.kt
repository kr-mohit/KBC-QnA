package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.utils.Response

interface MainRepository {

    suspend fun getQuestionsFromRemote(url: String): Response<List<Question>>

}