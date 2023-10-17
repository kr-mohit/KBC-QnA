package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.usecases.GetSelectedRegionUseCase
import com.pramoh.kbcqna.domain.usecases.GetSoundPreferenceUseCase
import com.pramoh.kbcqna.domain.usecases.SetSelectedRegionUseCase
import com.pramoh.kbcqna.domain.usecases.SetSoundPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSoundPreferenceUseCase: GetSoundPreferenceUseCase,
    private val setSoundPreferenceUseCase: SetSoundPreferenceUseCase,
    private val getSelectedRegionUseCase: GetSelectedRegionUseCase,
    private val setSelectedRegionUseCase: SetSelectedRegionUseCase
): ViewModel() {

    private val _isSoundOn = MutableLiveData<Boolean>()
    val isSoundOn: LiveData<Boolean>
        get() = _isSoundOn

    private val _isRegionIndia  = MutableLiveData<Boolean>()
    val isRegionIndia: LiveData<Boolean>
        get() = _isRegionIndia

    fun getDataFromSharedPref() {
        _isSoundOn.postValue(getSoundPreferenceUseCase.invoke())
        _isRegionIndia.postValue(getSelectedRegionUseCase.invoke() == "INDIA")
    }

    fun onSoundClicked() {
        if (isSoundOn.value == true) {
            _isSoundOn.postValue(false)
            setSoundPreferenceUseCase.invoke(false)
            // TODO: add code to turn sound off
        } else {
            _isSoundOn.postValue(true)
            setSoundPreferenceUseCase.invoke(true)
            // TODO: add code to turn sound on
        }

    }

    fun onRegionClicked() {
        if (isRegionIndia.value == true) {
            _isRegionIndia.postValue(false)
            setSelectedRegionUseCase.invoke("GLOBAL")
            // TODO: add code to change ques
        } else {
            _isRegionIndia.postValue(true)
            setSelectedRegionUseCase.invoke("INDIA")
            // TODO: add code to change ques
        }
    }
}