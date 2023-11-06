package com.pramoh.kbcqna.domain.repository

import com.pramoh.kbcqna.data.model.WonLostDataDTO
import com.pramoh.kbcqna.domain.model.WonLostData

interface SharedPrefRepository {

    fun setWonLostData(value: WonLostData)

    fun getWonLostData(): WonLostDataDTO

    fun setMusicOnOff(value: Boolean)

    fun getMusicOnOff(): Boolean

    fun setSfxAudioOnOff(value: Boolean)

    fun getSfxAudioOnOff(): Boolean

    fun setSelectedRegion(value: String)

    fun getSelectedRegion(): String

}