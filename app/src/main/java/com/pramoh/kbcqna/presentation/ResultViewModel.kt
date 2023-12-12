package com.pramoh.kbcqna.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.usecases.GetWonLostDataUseCase
import com.pramoh.kbcqna.domain.usecases.InsertPlayerToDBUseCase
import com.pramoh.kbcqna.domain.usecases.SetWonLostDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val setWonLostDataUseCase: SetWonLostDataUseCase,
    private val getWonLostDataUseCase: GetWonLostDataUseCase,
    private val insertPlayerToDBUseCase: InsertPlayerToDBUseCase
): ViewModel() {

    fun saveWonLostData(didUserWin: Boolean) {
        val savedData = getWonLostDataUseCase.invoke()
        if (didUserWin) {
            setWonLostDataUseCase.invoke(
                WonLostData(
                    wins = savedData.wins+1,
                    loses = savedData.loses)
            )
        } else {
            setWonLostDataUseCase.invoke(
                WonLostData(
                    wins = savedData.wins,
                    loses = savedData.loses+1)
            )
        }
    }

    fun insertPlayerToDB(player: PlayerData) {
        viewModelScope.launch {
            insertPlayerToDBUseCase.invoke(player)
        }
    }
}