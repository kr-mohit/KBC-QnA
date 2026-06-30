package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.domain.model.AppUpdateInfo
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.utils.Response

interface MainRepository {

    suspend fun getQuestionsFromRemote(url: String, questionCount: Int? = null): Response<List<Question>>

    suspend fun getQuestionsFromLocal(): Response<List<Question>>

    suspend fun getTopPlayers(): Response<List<PlayerData>>

    suspend fun insertPlayer(player: PlayerData)

    suspend fun deleteAllPlayers()

    suspend fun checkPlayerNameExists(name: String): Response<Boolean>

    suspend fun checkForAppUpdate(): Response<AppUpdateInfo>

    suspend fun getUniquePrizeAmounts(): List<Int>

    suspend fun getRemotePrizeAmounts(): Response<List<Int>>

    suspend fun updateRemotePrizeAmounts(prizeAmounts: List<Int>): Response<Unit>

    suspend fun updateRemoteAppUpdateInfo(newVersion: String, dialogType: String, updateMessage: String): Response<Unit>

    suspend fun getRemoteLeaderboardWithDocIds(): Response<List<com.pramoh.kbcqna.domain.model.LeaderboardPlayerWithId>>

    suspend fun deleteRemoteLeaderboardPlayers(docIds: List<String>): Response<Unit>

    suspend fun getRemoteQuestionStats(): Response<Map<Int, Int>>

    suspend fun updateRemoteMaintenanceInfo(isMaintenance: Boolean, message: String): Response<Unit>

    suspend fun addRemoteQuestion(question: Question): Response<Unit>
}