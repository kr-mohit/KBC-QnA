package com.pramoh.kbcqna.presentation.admin

data class AdminFeedbackOption(
    val docId: String,
    val name: String,
    val email: String,
    val type: String,
    val message: String,
    val timestamp: Long,
    var isSelected: Boolean = false
)