package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.data.MainRepository_Impl
import com.pramoh.kbcqna.domain.MainRepository

class SetWonLostDataUseCase(
    // TODO: get repository object here
) {

    private val repository: MainRepository = MainRepository_Impl()

    operator fun invoke(wins: Int) {
        repository.setWonLostData()
    }
}