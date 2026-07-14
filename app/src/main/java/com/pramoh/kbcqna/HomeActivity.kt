package com.pramoh.kbcqna

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
import com.google.firebase.messaging.FirebaseMessaging
import com.pramoh.kbcqna.databinding.ActivityHomeBinding
import com.pramoh.kbcqna.presentation.ExoplayerViewModel
import com.pramoh.kbcqna.presentation.home.HomeViewModel
import com.pramoh.kbcqna.utils.AdminBypassUtil
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import com.pramoh.kbcqna.utils.Response
import com.pramoh.kbcqna.utils.update.DownloadState
import com.pramoh.kbcqna.utils.update.UpdateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var updateManager: UpdateManager

    private lateinit var binding: ActivityHomeBinding
    private val exoplayerViewModel: ExoplayerViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var progressDialog: androidx.appcompat.app.AlertDialog? = null

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Notification permission granted
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        hideSystemBars()
        attachExoPlayer()
        observeAppUpdate()
        observeDownloadProgress()
        homeViewModel.checkForUpdates()
        checkAndRequestNotificationPermission()
        subscribeToFcmTopic()
    }

    private fun subscribeToFcmTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    private fun checkAndRequestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        exoplayerViewModel.onResume()
        checkPendingInstall()
    }

    private fun checkPendingInstall() {
        val forceUpdate = homeViewModel.appUpdateInfo.value?.data?.dialogType == "hard"
        val pendingFile = updateManager.pendingApkFile
        if (pendingFile != null && pendingFile.exists()) {
            if (updateManager.isInstallPermissionGranted()) {
                showInstallPendingDialog(pendingFile, forceUpdate)
            } else {
                if (forceUpdate) {
                    showPermissionRequiredDialog()
                } else {
                    updateManager.clearPendingUpdate()
                }
            }
        }
    }

    private fun showInstallPendingDialog(file: java.io.File, forceUpdate: Boolean) {
        if (forceUpdate) {
            val dialog = showCustomDialog(
                titleText = "Install Update",
                messageText = "The update has been downloaded. Please install it to continue playing.",
                negativeButtonText = "Install",
                negativeButtonAction = {
                    updateManager.installDownloadedApk(file)
                },
                isCancelable = false
            )
            val positiveButton =
                dialog.findViewById<android.widget.Button>(R.id.btn_popup_window_button_1)
            if (positiveButton != null) {
                positiveButton.text = getString(R.string.exit)
                positiveButton.visibility = android.view.View.VISIBLE
                positiveButton.setOnClickListener {
                    finish()
                }
            }
        } else {
            showCustomDialog(
                titleText = "Install Update",
                messageText = "The update has been downloaded. Would you like to install it now?",
                positiveButtonText = "Install",
                positiveButtonAction = {
                    updateManager.installDownloadedApk(file)
                    updateManager.clearPendingUpdate()
                },
                negativeButtonText = "Later",
                negativeButtonAction = {
                    updateManager.clearPendingUpdate()
                },
                isCancelable = true
            )
        }
    }

    private fun showPermissionRequiredDialog() {
        val dialog = showCustomDialog(
            titleText = "Permission Required",
            messageText = "Install permission is required to update and play the game. Please enable it in Settings.",
            negativeButtonText = "Settings",
            negativeButtonAction = {
                updateManager.guideToSettings()
            },
            isCancelable = false
        )
        val positiveButton =
            dialog.findViewById<android.widget.Button>(R.id.btn_popup_window_button_1)
        if (positiveButton != null) {
            positiveButton.text = getString(R.string.exit)
            positiveButton.visibility = android.view.View.VISIBLE
            positiveButton.setOnClickListener {
                finish()
            }
        }
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
                    MoneyTypeConversionUtil.prefix = updateInfo.currencyPrefix
                    MoneyTypeConversionUtil.suffix = updateInfo.currencySuffix
                    AdminBypassUtil.setAdminPasskey(updateInfo.adminPasskey)
                    if (updateInfo.isMaintenanceMode) {
                        showMaintenanceDialog(updateInfo.maintenanceMessage)
                    } else if (updateInfo.dialogType != "none") {
                        val currentVersion = getCurrentVersionName()
                        if (currentVersion != updateInfo.newVersion) {
                            showUpdateDialog(
                                updateInfo.dialogType,
                                updateInfo.updateMessage,
                                updateInfo.apkUrl
                            )
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

    private fun showUpdateDialog(dialogType: String, updateMessage: String, apkUrl: String?) {
        val displayMessage = updateMessage.ifBlank {
            if (dialogType == "hard")
                "A critical update is available. Please update the app to continue playing."
            else
                "A new update is available. Would you like to update now?"
        }

        val isForceUpdate = dialogType == "hard"
        val latestVersion = homeViewModel.appUpdateInfo.value?.data?.newVersion ?: "unknown"
        val titleText = "New Update Available (v$latestVersion)"

        if (isForceUpdate) {
            val dialog = showCustomDialog(
                titleText = titleText,
                messageText = displayMessage,
                negativeButtonText = "Update",
                negativeButtonAction = {
                    if (!apkUrl.isNullOrBlank()) {
                        updateManager.startUpdate(apkUrl)
                    } else {
                        redirectToPlayStore()
                        finish()
                    }
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
                titleText = titleText,
                messageText = displayMessage,
                positiveButtonText = "Update",
                positiveButtonAction = {
                    if (!apkUrl.isNullOrBlank()) {
                        updateManager.startUpdate(apkUrl)
                    } else {
                        redirectToPlayStore()
                    }
                },
                negativeButtonText = "Later",
                negativeButtonAction = {},
                isCancelable = true
            )
        }
    }

    private fun observeDownloadProgress() {
        updateManager.downloadState.observe(this) { state ->
            when (state) {
                is DownloadState.Idle -> {
                    dismissProgressDialog()
                }

                is DownloadState.Downloading -> {
                    showOrUpdateProgressDialog(
                        state.progress,
                        state.bytesDownloaded,
                        state.totalBytes
                    )
                }

                is DownloadState.Success -> {
                    dismissProgressDialog()
                    Toast.makeText(
                        this,
                        "Download complete. Starting installation...",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DownloadState.Error -> {
                    dismissProgressDialog()
                    showCustomDialog(
                        titleText = "Download Failed",
                        messageText = state.message,
                        negativeButtonText = "Close",
                        negativeButtonAction = {
                            updateManager.cancelUpdate()
                        },
                        isCancelable = true
                    )
                }
            }
        }
    }

    private fun showOrUpdateProgressDialog(progress: Int, downloaded: Long, total: Long) {
        if (progressDialog == null) {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this).create()
            val dialogView = layoutInflater.inflate(R.layout.dialog_download_progress, null)

            val forceUpdate = homeViewModel.appUpdateInfo.value?.data?.dialogType == "hard"
            val cancelButton =
                dialogView.findViewById<android.widget.Button>(R.id.btn_download_cancel)
            if (forceUpdate) {
                cancelButton?.visibility = android.view.View.GONE
            } else {
                cancelButton?.visibility = android.view.View.VISIBLE
                cancelButton?.setOnClickListener {
                    updateManager.cancelUpdate()
                    dialog.dismiss()
                }
            }

            dialog.setView(dialogView)
            dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                dialog.window?.insetsController?.apply {
                    hide(android.view.WindowInsets.Type.systemBars())
                    systemBarsBehavior =
                        android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

            progressDialog = dialog
        }

        val dialog = progressDialog ?: return
        val progressBar = dialog.findViewById<android.widget.ProgressBar>(R.id.pb_download_progress)
        val progressText = dialog.findViewById<android.widget.TextView>(R.id.tv_download_percentage)

        progressBar?.progress = progress

        val downloadedMb = downloaded.toDouble() / (1024 * 1024)
        val totalMb = total.toDouble() / (1024 * 1024)
        progressText?.text = String.format(
            java.util.Locale.US,
            "%d%% (%.2f MB / %.2f MB)",
            progress,
            downloadedMb,
            totalMb
        )
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
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
        val positiveButton =
            dialogView.findViewById<android.widget.Button>(R.id.btn_popup_window_button_1)
        val negativeButton =
            dialogView.findViewById<android.widget.Button>(R.id.btn_popup_window_button_2)
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
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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