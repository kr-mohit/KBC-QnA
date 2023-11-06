package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import javax.inject.Inject

class GetQuestionListUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke(url: String): Response<List<Question>> {
        return repository.getQuestionsFromRemote(url)
    }
}