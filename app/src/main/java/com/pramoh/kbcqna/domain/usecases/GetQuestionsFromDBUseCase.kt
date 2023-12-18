package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class GetQuestionsFromDBUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(level: Int): Response<List<Question>> {
        return repository.getQuestionsFromDB(level)
    }
}