package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.usecases.GetWonLostDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWonLostDataUseCase: GetWonLostDataUseCase
):ViewModel() {

    private val _wonLostData = MutableLiveData<WonLostData>()
    val wonLostData: LiveData<WonLostData>
        get() = _wonLostData

    private var onStartClicked: Boolean = false

    fun getWonLostData() {
        _wonLostData.postValue(getWonLostDataUseCase.invoke())
    }

    fun setOnStartClicked(value: Boolean) {
        onStartClicked = value
    }

    fun getOnStartClicked(): Boolean {
        return onStartClicked
    }
}