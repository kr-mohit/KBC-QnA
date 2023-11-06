package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class SetMusicPreferenceUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(value: Boolean) {
        return repository.setMusicOnOff(value)
    }
}