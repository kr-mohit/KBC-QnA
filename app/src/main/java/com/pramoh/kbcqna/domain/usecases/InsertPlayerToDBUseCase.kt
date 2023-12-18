package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class InsertPlayerToDBUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(player: PlayerData): Response<Boolean> {
        return repository.insertPlayerToDB(player)
    }
}