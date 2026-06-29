package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.utils.Response

interface MainRepository {

    suspend fun getQuestionsFromRemote(url: String): Response<List<Question>>

    suspend fun getQuestionsFromLocal(): Response<List<Question>>

    suspend fun getTopPlayers(): Response<List<PlayerData>>

    suspend fun insertPlayer(player: PlayerData)

    suspend fun deleteAllPlayers()

    suspend fun checkPlayerNameExists(name: String): Response<Boolean>
}