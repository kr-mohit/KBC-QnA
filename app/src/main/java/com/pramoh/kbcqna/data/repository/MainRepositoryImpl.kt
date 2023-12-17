package com.pramoh.kbcqna.data.repository

import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.db.OfflineQuestionsDB
import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.data.remote.QuestionsAPI
import com.pramoh.kbcqna.domain.model.OfflineQuestion
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import java.io.IOException

class MainRepositoryImpl(
    private val questionsAPI: QuestionsAPI,
    private val leaderboardDB: LeaderboardDB,
    private val offlineQuestionsDB: OfflineQuestionsDB
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

    override suspend fun getQuestionsFromDB(level: Int): Response<List<OfflineQuestion>> {
        return try {
            val response = offlineQuestionsDB.getOfflineQuestionsDAO().getQuestionsForLevel(level)
            Response.Success(response)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Unknown Error")
        }
    }
}