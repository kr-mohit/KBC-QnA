package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class SetSelectedRegionUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(value: String) {
        return repository.setSelectedRegion(value)
    }
}