package com.pramoh.kbcqna

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
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
        exoplayerViewModel.play()
    }

    override fun onPause() {
        super.onPause()
        exoplayerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoplayerViewModel.destroy()
    }

    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.navigationBarColor = getColor(R.color.metallic_violet)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun attachExoPlayer() {
        binding.exoplayerMusic.player = exoplayerViewModel.getMusicPlayer()
        binding.exoplayerSfxAudio.player = exoplayerViewModel.getSfxAudioPlayer()
    }
}