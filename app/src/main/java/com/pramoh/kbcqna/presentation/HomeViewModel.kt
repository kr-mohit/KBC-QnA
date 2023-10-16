package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.WonLostData

class HomeViewModel:ViewModel() {

    private val _wonLostData = MutableLiveData<WonLostData>()
    val wonLostData: LiveData<WonLostData>
        get() = _wonLostData

    fun getWonLostData() {
        // TODO: get data from shared pref
        _wonLostData.postValue(WonLostData(5, 2))
    }
}