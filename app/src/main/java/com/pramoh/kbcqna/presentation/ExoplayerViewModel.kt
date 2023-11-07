package com.pramoh.kbcqna.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.pramoh.kbcqna.domain.usecases.GetMusicPreferenceUseCase
import com.pramoh.kbcqna.domain.usecases.GetSfxAudioPreferenceUseCase
import com.pramoh.kbcqna.domain.usecases.SetMusicPreferenceUseCase
import com.pramoh.kbcqna.domain.usecases.SetSfxAudioPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ExoplayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getMusicPreferenceUseCase: GetMusicPreferenceUseCase,
    private val setMusicPreferenceUseCase: SetMusicPreferenceUseCase,
    private val setSfxAudioPreferenceUseCase: SetSfxAudioPreferenceUseCase,
    private val getSfxAudioPreferenceUseCase: GetSfxAudioPreferenceUseCase
): ViewModel() {

    private lateinit var musicTransitionState: MusicTransitionState

    private val _isMusicOn = MutableLiveData<Boolean>()
    val isMusicOn: LiveData<Boolean>
        get() = _isMusicOn

    private val _isSfxAudioOn = MutableLiveData<Boolean>()
    val isSfxAudioOn: LiveData<Boolean>
        get() = _isSfxAudioOn

    private var musicPlayer: ExoPlayer ?= null
    private var sfxAudioPlayer: ExoPlayer ?= null


    init {
        musicPlayer = ExoPlayer.Builder(context).build()
        sfxAudioPlayer = ExoPlayer.Builder(context).build()
    }

    fun getMusicSharedPref() {
        val value = getMusicPreferenceUseCase.invoke()
        _isMusicOn.postValue(value)
    }

    fun getSfxAudioSharedPref() {
        val value = getSfxAudioPreferenceUseCase.invoke()
        _isSfxAudioOn.postValue(value)
    }

    fun getMusicPlayer(): ExoPlayer? {
        return musicPlayer
    }

    fun getSfxAudioPlayer(): ExoPlayer? {
        return sfxAudioPlayer
    }

    fun getMusicTransitionState(): MusicTransitionState {
        return musicTransitionState
    }

    fun setMusicTransitionState(state: MusicTransitionState) {
        musicTransitionState = state
    }

    fun setupAndPlayMusicPlayer(audioRedId: Int) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        musicPlayer?.apply {
            if (currentMediaItem != mediaItem) {
                setMediaItem(mediaItem)
                playWhenReady = true
                prepare()
                play()
            }
        }
    }

    fun setupAndPlaySfxAudioPlayer(audioRedId: Int) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        sfxAudioPlayer?.apply {
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            play()
        }

    }

    fun play() {
        musicPlayer?.play()
        sfxAudioPlayer?.play()
    }

    fun pause() {
        musicPlayer?.pause()
        sfxAudioPlayer?.pause()
    }

    fun destroy() {
        stopMusicPlayer()
        stopSfxAudioPlayer()
        getMusicPlayer()?.release()
        getSfxAudioPlayer()?.release()
    }

    fun stopMusicPlayer() {
        musicPlayer?.stop()
        musicPlayer?.removeMediaItem(0)
    }

    fun stopSfxAudioPlayer() {
        sfxAudioPlayer?.stop()
        sfxAudioPlayer?.removeMediaItem(0)
    }

    fun setMusicOnOff() {
        if (isMusicOn.value == true) {
            _isMusicOn.postValue(false)
            setMusicPreferenceUseCase.invoke(false)
        } else {
            _isMusicOn.postValue(true)
            setMusicPreferenceUseCase.invoke(true)
        }
    }

    fun setSfxAudioOnOff() {
        if (isSfxAudioOn.value == true) {
            _isSfxAudioOn.postValue(false)
            setSfxAudioPreferenceUseCase.invoke(false)
        } else {
            _isSfxAudioOn.postValue(true)
            setSfxAudioPreferenceUseCase.invoke(true)
        }
    }

    enum class MusicTransitionState {
        QUESTIONNAIRE_TO_TICKTOCK, QUESTIONNAIRE_TO_SUSPENSE
    }
}