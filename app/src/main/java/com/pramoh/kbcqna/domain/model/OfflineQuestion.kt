package com.pramoh.kbcqna.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_question")
data class OfflineQuestion(

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
