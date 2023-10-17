package com.pramoh.kbcqna.data.model

import com.pramoh.kbcqna.domain.model.Question

data class QuestionDTO(
    val question: String?,
    val option1: String?,
    val option2: String?,
    val option3: String?,
    val option4: String?,
    val correctOptionNumber: Int?,
    val prizeAmount: String?,
    val region: String?
)

fun QuestionDTO.toDomainQuestion(): Question {
    return Question(
        question = this.question ?: "",
        option1 = this.option1 ?: "",
        option2 = this.option2 ?: "",
        option3 = this.option3 ?: "",
        option4 = this.option4 ?: "",
        correctOptionNumber = this.correctOptionNumber ?: 0,
        prizeAmount = this.prizeAmount ?: "",
        region = this.region ?: ""
    )
}
