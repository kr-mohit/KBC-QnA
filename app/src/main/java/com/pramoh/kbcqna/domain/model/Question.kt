package com.pramoh.kbcqna.domain.model

data class Question(
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctOptionNumber: Int,
    val prizeAmount: Int,
    val region: String
)
