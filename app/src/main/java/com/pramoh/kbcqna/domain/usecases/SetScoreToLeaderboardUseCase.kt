package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.LeaderboardData
import com.pramoh.kbcqna.domain.repository.MainRepository
import javax.inject.Inject

class SetScoreToLeaderboardUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(score: LeaderboardData) {
        repository.addScoreToLeaderBoardDatabase(score)
        // TODO: Add the score to db, and sort the leaderboard
    }
}