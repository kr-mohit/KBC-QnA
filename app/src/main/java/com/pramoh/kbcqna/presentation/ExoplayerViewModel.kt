package com.pramoh.kbcqna.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.pramoh.kbcqna.domain.usecases.GetSoundPreferenceUseCase
import com.pramoh.kbcqna.domain.usecases.SetSoundPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ExoplayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getSoundPreferenceUseCase: GetSoundPreferenceUseCase,
    private val setSoundPreferenceUseCase: SetSoundPreferenceUseCase
): ViewModel() {

    private val _isSoundOn = MutableLiveData<Boolean>()
    val isSoundOn: LiveData<Boolean>
        get() = _isSoundOn

    var player: ExoPlayer ?= null

    init {
        player = ExoPlayer.Builder(context).build()
    }

    fun getAudioSharedPref() {
        val value1 = getSoundPreferenceUseCase.invoke()
        _isSoundOn.postValue(value1)
    }

    fun setupAndPlay(audioRedId: Int) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        if (player?.currentMediaItem != mediaItem) {
            player?.setMediaItem(mediaItem)
            player?.playWhenReady = true
            player?.prepare()
            player?.play()
        }
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
        player?.removeMediaItem(0)
    }

    fun setSoundOnOff() {
        if (isSoundOn.value == true) {
            _isSoundOn.postValue(false)
            setSoundPreferenceUseCase.invoke(false)
        } else {
            _isSoundOn.postValue(true)
            setSoundPreferenceUseCase.invoke(true)
        }
    }
}