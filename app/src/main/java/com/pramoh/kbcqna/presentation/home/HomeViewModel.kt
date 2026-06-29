package com.pramoh.kbcqna.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.usecases.CheckPlayerNameExistsUseCase
import com.pramoh.kbcqna.domain.usecases.GetPlayerNameSharedPrefUseCase
import com.pramoh.kbcqna.domain.usecases.SetPlayerNameSharedPrefUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlayerNameSharedPrefUseCase: GetPlayerNameSharedPrefUseCase,
    private val setPlayerNameSharedPrefUseCase: SetPlayerNameSharedPrefUseCase,
    private val checkPlayerNameExistsUseCase: CheckPlayerNameExistsUseCase
): ViewModel() {

    private var onStartClicked: Boolean = false // TODO: Check if this can be removed
    private lateinit var currentPlayerName: String // TODO: Check if this should be passed in activities or used like this by two fragments

    private val _playerNameSharedPref = MutableLiveData<String>()
    val playerNameSharedPref: LiveData<String>
        get() = _playerNameSharedPref

    fun setOnStartClicked(value: Boolean) {
        onStartClicked = value
    }

    fun getOnStartClicked(): Boolean {
        return onStartClicked
    }

    fun setCurrentPlayerName(playerName: String) {
        currentPlayerName = playerName
    }

    fun getCurrentPlayerName(): String {
        return currentPlayerName
    }

    fun getPlayerNameSharedPref() {
        val name = getPlayerNameSharedPrefUseCase.invoke()
        _playerNameSharedPref.postValue(name)
    }

    fun setPlayerNameSharedPref(playerName: String) {
        setPlayerNameSharedPrefUseCase.invoke(playerName)
    }

    private val _playerNameCheckResult = MutableLiveData<Response<Boolean>?>()
    val playerNameCheckResult: LiveData<Response<Boolean>?>
        get() = _playerNameCheckResult

    fun checkPlayerNameExists(name: String) {
        _playerNameCheckResult.value = Response.Loading()
        viewModelScope.launch {
            _playerNameCheckResult.postValue(checkPlayerNameExistsUseCase.invoke(name))
        }
    }

    fun resetPlayerNameCheck() {
        _playerNameCheckResult.value = null
    }
}