package com.pramoh.kbcqna.domain.usecases

import android.util.Log
import com.pramoh.kbcqna.data.model.toDomainWonLostData
import com.pramoh.kbcqna.domain.model.WonLostData
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class GetWonLostDataUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(): WonLostData {
        val data = repository.getWonLostData().toDomainWonLostData()
        Log.d("idonnoe", "won-lost-data = $data")
        return data
    }
}