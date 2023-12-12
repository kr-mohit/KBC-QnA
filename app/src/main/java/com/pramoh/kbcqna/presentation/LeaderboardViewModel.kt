package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.usecases.ClearLeaderboardDataUseCase
import com.pramoh.kbcqna.domain.usecases.GetTopPlayersFromDBUseCase
import com.pramoh.kbcqna.domain.usecases.GetWonLostDataUseCase
import com.pramoh.kbcqna.domain.usecases.InsertPlayerToDBUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getWonLostDataUseCase: GetWonLostDataUseCase,
    private val getTopPlayersFromDBUseCase: GetTopPlayersFromDBUseCase,
    private val insertPlayerToDBUseCase: InsertPlayerToDBUseCase,
    private val clearLeaderboardDataUseCase: ClearLeaderboardDataUseCase
): ViewModel(){

    private val _wonLostData = MutableLiveData<WonLostData>()
    val wonLostData: LiveData<WonLostData>
        get() = _wonLostData

    private val _leaderboardList = MutableLiveData<Response<List<PlayerData>>>()
    val leaderboardList: LiveData<Response<List<PlayerData>>>
    get() = _leaderboardList

    fun getWonLostData() {
        _wonLostData.postValue(getWonLostDataUseCase.invoke())
    }

    fun getLeaderboardData() {
        viewModelScope.launch {
            _leaderboardList.postValue(getTopPlayersFromDBUseCase.invoke())
        }
    }

    fun insertPlayerToDB(player: PlayerData) {
        viewModelScope.launch {
            insertPlayerToDBUseCase.invoke(player)
            getLeaderboardData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearLeaderboardDataUseCase.invoke()
            getLeaderboardData()
        }
    }
}