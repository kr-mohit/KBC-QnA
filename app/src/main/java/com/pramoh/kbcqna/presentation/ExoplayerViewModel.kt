package com.pramoh.kbcqna.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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

    private var musicPlayer: ExoPlayer ?= null
    private var sfxAudioPlayer: ExoPlayer ?= null

    private val _isMusicOn = MutableLiveData<Boolean>()
    val isMusicOn: LiveData<Boolean>
        get() = _isMusicOn

    private val _isSfxAudioOn = MutableLiveData<Boolean>()
    val isSfxAudioOn: LiveData<Boolean>
        get() = _isSfxAudioOn

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

    fun getMusicPlayer(): ExoPlayer? {
        return musicPlayer
    }

    fun getSfxAudioPlayer(): ExoPlayer? {
        return sfxAudioPlayer
    }

    fun playMusic(audioRedId: Int, repeat: Boolean) {
        stopMusic()
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        musicPlayer?.apply {
            setMediaItem(mediaItem)
            repeatMode = (if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF)
            prepare()
            playWhenReady = true
        }
    }

    fun playSfxAudio(audioRedId: Int) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        sfxAudioPlayer?.apply {
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            play()
        }

    }

    fun stopMusic() {
        musicPlayer?.playWhenReady = false
        musicPlayer?.stop()
        musicPlayer?.removeMediaItem(0)
    }

    fun stopSfxAudio() {
        sfxAudioPlayer?.stop()
        sfxAudioPlayer?.removeMediaItem(0)
    }

    fun onResume() {
        musicPlayer?.play()
        sfxAudioPlayer?.play()
    }

    fun onPause() {
        musicPlayer?.pause()
        sfxAudioPlayer?.pause()
    }

    fun onDestroy() {
        stopMusic()
        stopSfxAudio()
        getMusicPlayer()?.release()
        getSfxAudioPlayer()?.release()
    }
}