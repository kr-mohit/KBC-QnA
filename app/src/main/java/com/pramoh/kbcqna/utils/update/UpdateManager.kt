package com.pramoh.kbcqna.utils.update

import android.app.DownloadManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val downloadManagerHelper = DownloadManagerHelper(context)
    private val installerHelper = InstallerHelper(context)

    private val _downloadState = MutableLiveData<DownloadState>(DownloadState.Idle)
    val downloadState: LiveData<DownloadState> get() = _downloadState

    private var currentDownloadId: Long? = null
    var pendingApkFile: File? = null
        private set
    private var queryJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun startUpdate(apkUrl: String) {
        if (_downloadState.value is DownloadState.Downloading) {
            return
        }

        _downloadState.value = DownloadState.Downloading(0, 0L, 0L)

        try {
            val fileName = "kbc_update.apk"
            val downloadId = downloadManagerHelper.startDownload(apkUrl, fileName)
            currentDownloadId = downloadId
            pendingApkFile = downloadManagerHelper.getDownloadFile(fileName)

            startQueryProgress(downloadId)
        } catch (e: Exception) {
            _downloadState.value =
                DownloadState.Error(e.localizedMessage ?: "Failed to start download")
        }
    }

    private fun startQueryProgress(downloadId: Long) {
        queryJob?.cancel()
        queryJob = coroutineScope.launch {
            var isDownloading = true

            while (isDownloading) {
                delay(500.milliseconds)
                val currentId = currentDownloadId ?: break
                val query = DownloadManager.Query().setFilterById(currentId)
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor != null && cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1

                    val downloadedIndex =
                        cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val downloaded =
                        if (downloadedIndex != -1) cursor.getLong(downloadedIndex) else 0L

                    val totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val total = if (totalIndex != -1) cursor.getLong(totalIndex) else 0L

                    when (status) {
                        DownloadManager.STATUS_RUNNING -> {
                            val progress =
                                if (total > 0) ((downloaded * 100) / total).toInt() else 0
                            _downloadState.postValue(
                                DownloadState.Downloading(
                                    progress,
                                    downloaded,
                                    total
                                )
                            )
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            isDownloading = false
                            val file = pendingApkFile
                            if (file != null && file.exists()) {
                                _downloadState.postValue(DownloadState.Success(file))
                                installOrRequestPermission(file)
                            } else {
                                _downloadState.postValue(DownloadState.Error("Downloaded file not found"))
                            }
                        }

                        DownloadManager.STATUS_FAILED -> {
                            isDownloading = false
                            val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                            val reason = if (reasonIndex != -1) cursor.getInt(reasonIndex) else -1
                            _downloadState.postValue(DownloadState.Error("Download failed. Code: $reason"))
                        }
                    }
                    cursor.close()
                } else {
                    cursor?.close()
                }
            }
        }
    }

    private fun installOrRequestPermission(file: File) {
        if (installerHelper.isUnknownSourcesPermissionGranted()) {
            installerHelper.installApk(context, file)
        } else {
            installerHelper.guideToUnknownSourcesSettings(context)
        }
    }

    fun isInstallPermissionGranted(): Boolean {
        return installerHelper.isUnknownSourcesPermissionGranted()
    }

    fun guideToSettings() {
        installerHelper.guideToUnknownSourcesSettings(context)
    }

    fun installDownloadedApk(file: File) {
        installerHelper.installApk(context, file)
    }

    fun clearPendingUpdate() {
        pendingApkFile = null
        _downloadState.postValue(DownloadState.Idle)
    }

    fun cancelUpdate() {
        currentDownloadId?.let {
            downloadManagerHelper.cancelDownload(it)
        }
        queryJob?.cancel()
        _downloadState.value = DownloadState.Idle
        pendingApkFile = null
        currentDownloadId = null
    }
}