package com.pramoh.kbcqna.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pramoh.kbcqna.domain.model.Question

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctOptionNumber: Int,
    val prizeAmount: Int,
    val region: String
)

fun QuestionEntity.toDomain() = Question(
    question = question,
    option1 = option1,
    option2 = option2,
    option3 = option3,
    option4 = option4,
    correctOptionNumber = correctOptionNumber,
    prizeAmount = prizeAmount,
    region = region
)