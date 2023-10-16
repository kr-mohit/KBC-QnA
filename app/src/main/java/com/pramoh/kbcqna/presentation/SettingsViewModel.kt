package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    private val _isSoundOn = MutableLiveData<Boolean>()
    val isSoundOn: LiveData<Boolean>
        get() = _isSoundOn

    private val _isRegionIndia  = MutableLiveData<Boolean>()
    val isRegionIndia: LiveData<Boolean>
        get() = _isRegionIndia

    init {
        _isSoundOn.value = false
        _isRegionIndia.value = true
    }

    fun onSoundClicked() {
        if (isSoundOn.value == true) {
            _isSoundOn.postValue(false) //remove this and save in shared pref
            // TODO: add code to turn sound off
        } else {
            _isSoundOn.postValue(true) //remove this and save in shared pref
            // TODO: add code to turn sound on
        }

    }

    fun onRegionClicked() {
        if (isRegionIndia.value == true) {
            _isRegionIndia.postValue(false) //remove this and save in shared pref
            // TODO: add code to change ques
        } else {
            _isRegionIndia.postValue(true) //remove this and save in shared pref
            // TODO: add code to change ques
        }
    }
}