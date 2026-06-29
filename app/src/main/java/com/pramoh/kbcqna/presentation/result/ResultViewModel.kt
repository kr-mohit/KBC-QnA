package com.pramoh.kbcqna.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.domain.usecases.InsertPlayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val insertPlayerUseCase: InsertPlayerUseCase
): ViewModel() {

    fun insertPlayer(player: PlayerData) {
        viewModelScope.launch {
            insertPlayerUseCase.invoke(player)
        }
    }
}