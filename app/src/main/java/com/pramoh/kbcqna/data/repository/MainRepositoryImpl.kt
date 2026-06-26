package com.pramoh.kbcqna.data.repository

import android.content.Context
import com.google.gson.Gson
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.model.ApiDataDTO
import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.data.remote.QuestionsAPI
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import java.io.IOException

class MainRepositoryImpl(
    private val context: Context,
    private val questionsAPI: QuestionsAPI,
    private val leaderboardDB: LeaderboardDB
): MainRepository {

    override suspend fun getQuestionsFromLocal(): Response<List<Question>> {
        return try {
            val jsonString = context.resources.openRawResource(R.raw.questions).bufferedReader().use { it.readText() }
            val apiDataDTO = Gson().fromJson(jsonString, ApiDataDTO::class.java)
            val allQuestions = apiDataDTO.data.map { it.toDomainQuestion() }

            val prizeAmounts = listOf(
                1000, 5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000,
                1250000, 2500000, 5000000, 10000000, 30000000, 100000000
            )

            val selectedQuestions = prizeAmounts.mapNotNull { prize ->
                allQuestions.filter { it.prizeAmount == prize }.randomOrNull()
            }

            if (selectedQuestions.size == 15) {
                Response.Success(selectedQuestions)
            } else {
                Response.Error("Insufficient local questions parsed. Expected 15, got ${selectedQuestions.size}")
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to parse local questions")
        }
    }

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

    override suspend fun getTopPlayersFromDB(): Response<List<PlayerData>> {
        return try {
            val response = leaderboardDB.getLeaderboardDAO().getTopPlayers()
            Response.Success(response)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun insertPlayerToDB(player: PlayerData) {
        val currentTopPlayers = leaderboardDB.getLeaderboardDAO().getTopPlayers()

        if (currentTopPlayers.size < 10) {
            leaderboardDB.getLeaderboardDAO().insertPlayer(player)
        } else {
            val lowestTopPlayer = currentTopPlayers.minByOrNull { it.moneyWon }

            if (lowestTopPlayer != null && player.moneyWon > lowestTopPlayer.moneyWon) {
                leaderboardDB.getLeaderboardDAO().insertPlayer(player)
                leaderboardDB.getLeaderboardDAO().deletePlayer(lowestTopPlayer)
            }
        }
    }

    override suspend fun deleteAllPlayersInDB() {
        leaderboardDB.getLeaderboardDAO().deleteAllPlayers()
    }
}