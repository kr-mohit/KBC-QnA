package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.usecases.GetPlayerNameSharedPrefUseCase
import com.pramoh.kbcqna.domain.usecases.SetPlayerNameSharedPrefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlayerNameSharedPrefUseCase: GetPlayerNameSharedPrefUseCase,
    private val setPlayerNameSharedPrefUseCase: SetPlayerNameSharedPrefUseCase
): ViewModel() {

    private val _playerNameSharedPref = MutableLiveData<String>()
    val playerNameSharedPref: LiveData<String>
        get() = _playerNameSharedPref

    fun getPlayerNameSharedPref() {
        val name = getPlayerNameSharedPrefUseCase.invoke()
        _playerNameSharedPref.postValue(name)
    }

    fun setPlayerNameSharedPref(playerName: String) {
        setPlayerNameSharedPrefUseCase.invoke(playerName)
    }

}