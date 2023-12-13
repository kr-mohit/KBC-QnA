package com.pramoh.kbcqna.domain.repository

interface SharedPrefRepository {

    fun setMusicOnOff(value: Boolean)

    fun getMusicOnOff(): Boolean

    fun setSfxAudioOnOff(value: Boolean)

    fun getSfxAudioOnOff(): Boolean

    fun setSelectedRegion(value: String)

    fun getSelectedRegion(): String

    fun setPlayerName(value: String)

    fun getPlayerName(): String

}