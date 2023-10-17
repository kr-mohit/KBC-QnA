package com.pramoh.kbcqna.data.model

import com.pramoh.kbcqna.domain.model.WonLostData

data class WonLostDataDTO(
    val wins: Int?,
    val loses: Int?
)

fun WonLostDataDTO.toDomainWonLostData(): WonLostData {
    return WonLostData(
        wins = this.wins ?: 0,
        loses = this.loses ?: 0
    )
}
