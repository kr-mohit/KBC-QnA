package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class CheckPlayerNameExistsUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(name: String): Response<Boolean> {
        return repository.checkPlayerNameExists(name)
    }
}
