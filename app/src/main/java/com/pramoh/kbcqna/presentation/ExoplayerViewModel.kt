package com.pramoh.kbcqna.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
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

    var musicPlayer: ExoPlayer
    var sfxAudioPlayer: ExoPlayer

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

    fun initializeSfxAudioPlayer(audioRedId: Int) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        sfxAudioPlayer.let {
            it.setMediaItem(mediaItem)
            it.prepare()
        }
    }

    fun playMusic(audioRedId: Int, repeat: Boolean) {
        val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(audioRedId))
        musicPlayer.let {
            it.setMediaItem(mediaItem)
            it.repeatMode = (if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF)
            it.prepare()
            it.play()
        }
    }

    fun stopMusic() {
        musicPlayer.playWhenReady = false
        musicPlayer.stop()
        musicPlayer.removeMediaItem(0)
    }

    fun playSfxAudio() {
        sfxAudioPlayer.seekTo(0)
        sfxAudioPlayer.play()

    }

    fun onResume() {
        musicPlayer.play()
        sfxAudioPlayer.play()
    }

    fun onPause() {
        musicPlayer.pause()
        sfxAudioPlayer.pause()
    }

    fun onDestroy() {
        musicPlayer.stop()
        musicPlayer.removeMediaItem(0)
        musicPlayer.release()

        sfxAudioPlayer.stop()
        sfxAudioPlayer.removeMediaItem(0)
        sfxAudioPlayer.release()
    }
}