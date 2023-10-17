package com.pramoh.kbcqna.data

import com.pramoh.kbcqna.domain.MainRepository

class MainRepository_Impl: MainRepository {

    override fun setWonLostData() {
        // TODO:  
    }

    override fun getWonLostData() {
        TODO("Not yet implemented")
    }

    override fun setSoundOnOff() {
        TODO("Not yet implemented")
    }

    override fun getSoundOnOff() {
        TODO("Not yet implemented")
    }

    override fun setRegion() {
        TODO("Not yet implemented")
    }

    override fun getRegion() {
        TODO("Not yet implemented")
    }

    override fun getQuestions() {
        TODO("Not yet implemented")
    }

    private fun saveDataToSharedPref(key: String, value: String) {
        // TODO: save data by enc shared pref
    }

    private fun getDataFromSharedPref(key: String): String {
        // TODO: get data by enc shared pref
        return "Hi"
    }
}