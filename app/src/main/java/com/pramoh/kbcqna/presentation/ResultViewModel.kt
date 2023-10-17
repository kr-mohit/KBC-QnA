package com.pramoh.kbcqna.presentation

import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.usecases.SetWonLostDataUseCase

class ResultViewModel(
    // TODO: get usecase object here
): ViewModel() {

    private val setWonLostDataUseCase = SetWonLostDataUseCase()

    fun saveWonLostData(hasWon: Boolean) {
        setWonLostDataUseCase.invoke(5) // TODO: change here
    }
}