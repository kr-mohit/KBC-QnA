package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.MainRepository
import javax.inject.Inject

class ClearLeaderboardDataUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke() {
        return repository.deleteAllPlayersInDB()
    }
}