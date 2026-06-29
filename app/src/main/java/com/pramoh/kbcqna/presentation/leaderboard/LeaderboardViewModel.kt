package com.pramoh.kbcqna.presentation.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.usecases.ClearLeaderboardDataUseCase
import com.pramoh.kbcqna.domain.usecases.GetTopPlayersUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getTopPlayersUseCase: GetTopPlayersUseCase,
    private val clearLeaderboardDataUseCase: ClearLeaderboardDataUseCase
): ViewModel(){

    private val _leaderboardList = MutableLiveData<Response<List<PlayerData>>>()
    val leaderboardList: LiveData<Response<List<PlayerData>>>
    get() = _leaderboardList

    fun getLeaderboardData() {
        viewModelScope.launch {
            _leaderboardList.postValue(getTopPlayersUseCase.invoke())
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearLeaderboardDataUseCase.invoke()
            getLeaderboardData()
        }
    }
}