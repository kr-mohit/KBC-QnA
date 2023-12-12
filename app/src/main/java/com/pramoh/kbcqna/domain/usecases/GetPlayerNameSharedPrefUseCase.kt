package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class GetPlayerNameSharedPrefUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(): String {
        return repository.getPlayerName()
    }
}