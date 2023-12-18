package com.pramoh.kbcqna.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pramoh.kbcqna.domain.model.Question

@Entity(tableName = "offline_question")
data class QuestionDTO(

    @PrimaryKey
    @ColumnInfo(name = "question_id")
    val questionId: Int?,

    @ColumnInfo(name = "question_text")
    val questionText: String?,

    @ColumnInfo(name = "option1")
    val optionA: String?,

    @ColumnInfo(name = "option2")
    val optionB: String?,

    @ColumnInfo(name = "option3")
    val optionC: String?,

    @ColumnInfo(name = "option4")
    val optionD: String?,

    @ColumnInfo(name = "correct_option_number")
    val correctOptionNumber: Int?,

    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: Int?,

    @ColumnInfo(name = "region")
    val region: String?,

    @ColumnInfo(name = "question_level_id")
    val questionLevelId: Int?

)

fun QuestionDTO.toDomainQuestion(): Question { // TODO: Remove this, have to do it the other way. This is messy. Or set the data type of both the questions as same.
    return Question(
        question = this.questionText ?: "",
        option1 = this.optionA ?: "",
        option2 = this.optionB ?: "",
        option3 = this.optionC ?: "",
        option4 = this.optionD ?: "",
        correctOptionNumber = this.correctOptionNumber ?: 0,
        prizeAmount = getPrizeAmountFromDifficultyLevel(this.difficultyLevel),
        region = this.region ?: "",
    )
}

private fun getPrizeAmountFromDifficultyLevel(level: Int?): Int {
    return when (level) {
        1 -> 1000
        2 -> 5000
        3 -> 10000
        4 -> 20000
        5 -> 40000
        6 -> 80000
        7 -> 160000
        8 -> 320000
        9 -> 640000
        10 -> 1250000
        11 -> 2500000
        12 -> 5000000
        13 -> 10000000
        14 -> 30000000
        15 -> 100000000
        else -> 0
    }
}
