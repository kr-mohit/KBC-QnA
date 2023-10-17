package com.pramoh.kbcqna.domain

interface MainRepository {

    fun setWonLostData()

    fun getWonLostData()

    fun setSoundOnOff()

    fun getSoundOnOff()

    fun setRegion()

    fun getRegion()

    fun getQuestions()

}