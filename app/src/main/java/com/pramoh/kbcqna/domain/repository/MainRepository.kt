package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.domain.model.LeaderboardData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.utils.Response

interface MainRepository {

    suspend fun getQuestionsFromRemote(url: String): Response<List<Question>>

    suspend fun getLeaderboardDataFromDatabase(): Response<List<LeaderboardData>>

    suspend fun addScoreToLeaderBoardDatabase(score: LeaderboardData): Boolean

    suspend fun deleteLeaderboardData()

}