package com.pramoh.kbcqna.data.repository

import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.data.remote.QuestionsAPI
import com.pramoh.kbcqna.domain.model.LeaderboardData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import java.io.IOException

class MainRepositoryImpl(
    private val questionsAPI: QuestionsAPI,
    private val leaderboardDB: LeaderboardDB
): MainRepository {

    override suspend fun getQuestionsFromRemote(url: String): Response<List<Question>> {
        return try {
            val response = questionsAPI.getQuestionsFromUrl(url)
            Response.Success(response.data.map { it.toDomainQuestion() })
        } catch (e: IOException) {
            Response.Error(e.localizedMessage ?: "Check Internet Connection")
        } catch (e: Exception) {
            Response.Error(e.localizedMessage?: "Unknown Error")
        }
    }

    override suspend fun getLeaderboardDataFromDatabase(): Response<List<LeaderboardData>> {
        return try {
            val response = leaderboardDB.getLeaderboardDAO().getScores()
            Response.Success(response)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun addScoreToLeaderBoardDatabase(score: LeaderboardData): Boolean {
        leaderboardDB.getLeaderboardDAO().addScore(score)
        return true
    }

    override suspend fun deleteLeaderboardData() {
        leaderboardDB.getLeaderboardDAO().deleteAll()
    }
}