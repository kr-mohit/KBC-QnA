package com.pramoh.kbcqna.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.usecases.GetSelectedRegionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSelectedRegionUseCase: GetSelectedRegionUseCase
): ViewModel() {

    private val _isRegionIndia  = MutableLiveData<Boolean>()
    val isRegionIndia: LiveData<Boolean>
        get() = _isRegionIndia

    fun getRegionSharedPref() {
        _isRegionIndia.postValue(getSelectedRegionUseCase.invoke() == "INDIA")
    }

    /*
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
    */
}