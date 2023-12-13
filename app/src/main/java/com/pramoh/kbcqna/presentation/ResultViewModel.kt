package com.pramoh.kbcqna.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.usecases.InsertPlayerToDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val insertPlayerToDBUseCase: InsertPlayerToDBUseCase
): ViewModel() {

    fun insertPlayerToDB(player: PlayerData) {
        viewModelScope.launch {
            insertPlayerToDBUseCase.invoke(player)
        }
    }
}