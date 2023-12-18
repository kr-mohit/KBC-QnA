package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.usecases.GetPlayerNameSharedPrefUseCase
import com.pramoh.kbcqna.domain.usecases.InsertPlayerToDBUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val insertPlayerToDBUseCase: InsertPlayerToDBUseCase,
    private val getPlayerNameSharedPrefUseCase: GetPlayerNameSharedPrefUseCase
): ViewModel() {

    private val _playerNameSharedPref = MutableLiveData<String>()
    val playerNameSharedPref: LiveData<String>
        get() = _playerNameSharedPref

    private val _didPlayerInsert = MutableLiveData<Response<Boolean>>()
    val didPlayerInsert: LiveData<Response<Boolean>>
        get() = _didPlayerInsert

    fun insertPlayerToDB(player: PlayerData) {
        viewModelScope.launch {
            _didPlayerInsert.postValue(insertPlayerToDBUseCase.invoke(player))
        }
    }

    fun getPlayerNameSharedPref() {
        val name = getPlayerNameSharedPrefUseCase.invoke()
        _playerNameSharedPref.postValue(name)
    }
}