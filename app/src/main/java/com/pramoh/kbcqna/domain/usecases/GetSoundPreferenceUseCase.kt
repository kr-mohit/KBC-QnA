package com.pramoh.kbcqna.domain.usecases

import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import javax.inject.Inject

class GetSoundPreferenceUseCase @Inject constructor(
    private val repository: SharedPrefRepository
) {

    operator fun invoke(): Boolean {
        return repository.getSoundOnOff()
    }
}