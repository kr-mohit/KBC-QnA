package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.LeaderboardData
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class GetLeaderboardDataUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(): Response<List<LeaderboardData>> {
        return repository.getLeaderboardDataFromDatabase()
    }
}