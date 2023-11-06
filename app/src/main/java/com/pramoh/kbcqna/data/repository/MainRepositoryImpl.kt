package com.pramoh.kbcqna.data.repository

import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.data.remote.QuestionsAPI
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import java.io.IOException

class MainRepositoryImpl(private val questionsAPI: QuestionsAPI): MainRepository {

    override suspend fun getQuestionsFromRemote(url: String): Response<List<Question>> {
        return try {
            val response1 = questionsAPI.getQuestionsFromUrl(url)
            Response.Success(response1.data.map { it.toDomainQuestion() })
        } catch (e: IOException) {
            Response.Error(e.localizedMessage ?: "Check Internet Connection")
        } catch (e: Exception) {
            Response.Error(e.localizedMessage?: "Unknown Error")
        }
    }
}