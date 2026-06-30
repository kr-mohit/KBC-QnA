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
import androidx.core.graphics.drawable.toDrawable
import com.pramoh.kbcqna.utils.AdminBypassUtil

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
                    AdminBypassUtil.setAdminPasskey(updateInfo.adminPasskey)
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

        val dialog = showCustomDialog(
            titleText = "Server Maintenance",
            messageText = displayMessage,
            negativeButtonText = "Exit",
            negativeButtonAction = {
                finish()
            },
            isCancelable = false
        )

        val textView = dialog.findViewById<android.widget.TextView>(R.id.tv_popup_window_text)
        if (textView != null) {
            AdminBypassUtil.attachBypass(textView) {
                dialog.dismiss()
            }
        }
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

        if (dialogType == "hard") {
            val dialog = showCustomDialog(
                titleText = "New Update Available",
                messageText = displayMessage,
                negativeButtonText = "Update",
                negativeButtonAction = {
                    redirectToPlayStore()
                    finish()
                },
                isCancelable = false
            )

            val textView = dialog.findViewById<android.widget.TextView>(R.id.tv_popup_window_text)
            if (textView != null) {
                AdminBypassUtil.attachBypass(textView) {
                    dialog.dismiss()
                }
            }
        } else {
            showCustomDialog(
                titleText = "New Update Available",
                messageText = displayMessage,
                positiveButtonText = "Update",
                positiveButtonAction = {
                    redirectToPlayStore()
                },
                negativeButtonText = "Later",
                negativeButtonAction = {},
                isCancelable = true
            )
        }
    }

    private fun redirectToPlayStore() {
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
    }

    private fun showCustomDialog(
        titleText: String? = null,
        messageText: String? = null,
        positiveButtonText: String? = null,
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        isCancelable: Boolean = false
    ): androidx.appcompat.app.AlertDialog {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_box, null)

        val titleView = dialogView.findViewById<android.widget.TextView>(R.id.tv_popup_window_title)
        val textView = dialogView.findViewById<android.widget.TextView>(R.id.tv_popup_window_text)
        val positiveButton = dialogView.findViewById<android.widget.Button>(R.id.btn_popup_window_button_1)
        val negativeButton = dialogView.findViewById<android.widget.Button>(R.id.btn_popup_window_button_2)
        val layoutButtons = dialogView.findViewById<android.view.View>(R.id.layout_buttons)

        if (!titleText.isNullOrBlank()) {
            titleView.text = titleText
            titleView.visibility = android.view.View.VISIBLE
        } else {
            titleView.visibility = android.view.View.GONE
        }

        if (!messageText.isNullOrBlank()) {
            textView.text = messageText
            textView.visibility = android.view.View.VISIBLE
        } else {
            textView.visibility = android.view.View.GONE
        }

        if (!positiveButtonText.isNullOrBlank()) {
            positiveButton.text = positiveButtonText
            positiveButton.visibility = android.view.View.VISIBLE
            positiveButton.setOnClickListener {
                positiveButtonAction?.invoke()
                dialog.dismiss()
            }
        } else {
            positiveButton.visibility = android.view.View.GONE
        }

        if (!negativeButtonText.isNullOrBlank()) {
            negativeButton.text = negativeButtonText
            negativeButton.visibility = android.view.View.VISIBLE
            negativeButton.setOnClickListener {
                negativeButtonAction?.invoke()
                dialog.dismiss()
            }
        } else {
            negativeButton.visibility = android.view.View.GONE
        }

        if (positiveButtonText.isNullOrBlank() && negativeButtonText.isNullOrBlank()) {
            layoutButtons.visibility = android.view.View.GONE
        } else {
            layoutButtons.visibility = android.view.View.VISIBLE
        }

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)
        dialog.show()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            dialog.window?.insetsController?.apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            dialog.window?.decorView?.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
                            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        return dialog
    }
}