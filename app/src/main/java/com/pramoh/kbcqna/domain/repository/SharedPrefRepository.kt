package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.data.model.WonLostDataDTO
import com.pramoh.kbcqna.domain.model.WonLostData

interface SharedPrefRepository {

    fun setWonLostData(value: WonLostData)

    fun getWonLostData(): WonLostDataDTO

    fun setSoundOnOff(value: Boolean)

    fun getSoundOnOff(): Boolean

    fun setSelectedRegion(value: String)

    fun getSelectedRegion(): String

}