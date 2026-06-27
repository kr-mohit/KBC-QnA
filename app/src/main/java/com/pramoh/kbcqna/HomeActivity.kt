package com.pramoh.kbcqna

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
import com.pramoh.kbcqna.databinding.ActivityHomeBinding
import com.pramoh.kbcqna.presentation.ExoplayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val exoplayerViewModel: ExoplayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        hideSystemBars()
        attachExoPlayer()
    }

    override fun onResume() {
        super.onResume()
        exoplayerViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        exoplayerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoplayerViewModel.onDestroy()
    }

    private fun hideSystemBars() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }

    @OptIn(UnstableApi::class)
    private fun attachExoPlayer() {
        binding.exoplayerMusic.player = exoplayerViewModel.musicPlayer
        binding.exoplayerSfxAudio.player = exoplayerViewModel.sfxAudioPlayer
        exoplayerViewModel.initializeSfxAudioPlayer(R.raw.audio_button_click)
    }
}