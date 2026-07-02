package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class SubmitFeedbackUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        type: String,
        message: String
    ): Response<Unit> {
        return mainRepository.submitFeedback(name, email, type, message)
    }
}