package com.pramoh.kbcqna.domain.model

data class AppUpdateInfo(
    val newVersion: String,
    val dialogType: String,
    val updateMessage: String
)
