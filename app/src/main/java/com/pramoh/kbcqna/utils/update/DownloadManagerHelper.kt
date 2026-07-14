package com.pramoh.kbcqna.utils.update

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import androidx.core.net.toUri

class DownloadManagerHelper(private val context: Context) {

    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun startDownload(url: String, fileName: String): Long {
        // Clean up previous files
        val destinationFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val request = DownloadManager.Request(url.toUri())
            .setTitle("Downloading App Update")
            .setDescription("Please wait while the update is downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(destinationFile))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        return downloadManager.enqueue(request)
    }

    fun getDownloadFile(fileName: String): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }
}