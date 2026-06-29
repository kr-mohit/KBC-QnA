package com.pramoh.kbcqna

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import com.pramoh.kbcqna.databinding.ActivityHomeBinding
import com.pramoh.kbcqna.presentation.ExoplayerViewModel
import com.pramoh.kbcqna.presentation.home.HomeViewModel
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val exoplayerViewModel: ExoplayerViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        hideSystemBars()
        attachExoPlayer()
        observeAppUpdate()
        homeViewModel.checkForUpdates()
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

    private fun observeAppUpdate() {
        homeViewModel.appUpdateInfo.observe(this) { response ->
            if (response is Response.Success) {
                val updateInfo = response.data
                if (updateInfo != null) {
                    if (updateInfo.isMaintenanceMode) {
                        showMaintenanceDialog(updateInfo.maintenanceMessage)
                    } else if (updateInfo.dialogType != "none") {
                        val currentVersion = getCurrentVersionName()
                        if (currentVersion != updateInfo.newVersion) {
                            showUpdateDialog(updateInfo.dialogType, updateInfo.updateMessage)
                        }
                    }
                }
            }
        }
    }

    private fun showMaintenanceDialog(maintenanceMessage: String) {
        val displayMessage = maintenanceMessage.ifBlank {
            "The server is currently undergoing maintenance. Please try again later."
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Server Maintenance")
            .setMessage(displayMessage)
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun getCurrentVersionName(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    private fun showUpdateDialog(dialogType: String, updateMessage: String) {
        val displayMessage = updateMessage.ifBlank {
            if (dialogType == "hard")
                "A critical update is available. Please update the app to continue playing."
            else
                "A new update is available. Would you like to update now?"
        }

        val builder = android.app.AlertDialog.Builder(this)
            .setTitle("New Update Available")
            .setMessage(displayMessage)
            .setPositiveButton("Update") { _, _ ->
                val playStoreIntent = android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    "market://details?id=$packageName".toUri()
                )
                try {
                    startActivity(playStoreIntent)
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(
                        android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$packageName".toUri()
                        )
                    )
                }
                if (dialogType == "hard") {
                    finish()
                }
            }

        if (dialogType == "soft") {
            builder.setNegativeButton("Later") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        if (dialogType == "hard") {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
        dialog.show()
    }
}