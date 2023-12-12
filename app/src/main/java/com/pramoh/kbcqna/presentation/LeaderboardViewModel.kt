package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.LeaderboardData
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.usecases.GetLeaderboardDataUseCase
import com.pramoh.kbcqna.domain.usecases.GetWonLostDataUseCase
import com.pramoh.kbcqna.domain.usecases.SetScoreToLeaderboardUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getWonLostDataUseCase: GetWonLostDataUseCase,
    private val getLeaderboardDataUseCase: GetLeaderboardDataUseCase,
    private val setScoreToLeaderboardUseCase: SetScoreToLeaderboardUseCase
): ViewModel(){

    private val _wonLostData = MutableLiveData<WonLostData>()
    val wonLostData: LiveData<WonLostData>
        get() = _wonLostData

    private val _leaderboardList = MutableLiveData<Response<List<LeaderboardData>>>()
    val leaderboardList: LiveData<Response<List<LeaderboardData>>>
    get() = _leaderboardList

    fun getWonLostData() {
        _wonLostData.postValue(getWonLostDataUseCase.invoke())
    }

    fun getLeaderboardTable() {
        viewModelScope.launch {
            _leaderboardList.postValue(getLeaderboardDataUseCase.invoke())
        }
    }

    fun addScoreToDB(score: LeaderboardData) {
        viewModelScope.launch {
            setScoreToLeaderboardUseCase.invoke(score)
        }
    }
}