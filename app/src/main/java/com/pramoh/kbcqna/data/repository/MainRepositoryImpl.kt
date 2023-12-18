package com.pramoh.kbcqna.data.repository

import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.db.OfflineQuestionsDB
import com.pramoh.kbcqna.data.model.PlayerDataDTO
import com.pramoh.kbcqna.data.model.toDomainPlayerData
import com.pramoh.kbcqna.data.model.toDomainQuestion
import com.pramoh.kbcqna.data.remote.QuestionsAPI
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
            Response.Success(response.map { it.toDomainQuestion() })
        } catch (e: IOException) {
            Response.Error(e.localizedMessage ?: "Check Internet Connection")
        } catch (e: Exception) {
            Response.Error(e.localizedMessage?: "Unknown Error")
        }
    }

    override suspend fun getTopPlayersFromDB(): Response<List<PlayerData>> {
        return try {
            val response = leaderboardDB.getLeaderboardDAO().getTopPlayers()
            Response.Success(response.map { it.toDomainPlayerData() })
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun insertPlayerToDB(player: PlayerData): Response<Boolean> {

        when (val currentTopPlayers = getTopPlayersFromDB()) {
            is Response.Success -> {
                val playerDTO = PlayerDataDTO(player.id, player.playerName, player.moneyWon)
                currentTopPlayers.data?.let { list ->
                    if (list.size < 10) {
                        leaderboardDB.getLeaderboardDAO().insertPlayer(playerDTO)
                    } else {
                        val lowestTopPlayer = list.minByOrNull { it.moneyWon }
                        if (lowestTopPlayer != null && player.moneyWon > lowestTopPlayer.moneyWon) {
                            leaderboardDB.getLeaderboardDAO().insertPlayer(playerDTO)
                            leaderboardDB.getLeaderboardDAO().deletePlayer(PlayerDataDTO(lowestTopPlayer.id, lowestTopPlayer.playerName, lowestTopPlayer.moneyWon))
                        }
                    }
                }
                return Response.Success(true)
            }
            is Response.Error -> {
                return Response.Error(currentTopPlayers.error ?: "Unknown Error")
            }
            is Response.Loading -> {
                return Response.Loading()
            }
        }
    }

    override suspend fun deleteAllPlayersInDB(): Response<Boolean> {
        leaderboardDB.getLeaderboardDAO().deleteAllPlayers()
        return when(val list = getTopPlayersFromDB()) {
            is Response.Error -> Response.Error(list.error ?: "Unknown Error")
            is Response.Loading -> Response.Loading()
            is Response.Success -> {
                    if (list.data?.size == 0) Response.Success(true) else Response.Error("Unknown Error")
            }
        }
    }

    override suspend fun getQuestionsFromDB(level: Int): Response<List<Question>> {
        return try {
            val response = offlineQuestionsDB.getOfflineQuestionsDAO().getQuestionsForLevel(level)
            Response.Success(response.map { it.toDomainQuestion() })
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Unknown Error")
        }
    }
}