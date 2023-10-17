package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class SetWonLostDataUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(newData: WonLostData) {
        repository.setWonLostData(newData)
    }
}