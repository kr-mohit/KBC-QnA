package com.pramoh.kbcqna.domain.model

data class AppUpdateInfo(
    val newVersion: String,
    val dialogType: String,
    val updateMessage: String,
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "",
    val adminPasskey: String? = null,
    val currencyPrefix: String = "",
    val currencySuffix: String = "",
    val apkUrl: String? = null
)