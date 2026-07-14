package com.pramoh.kbcqna.utils.update

import java.io.File

sealed interface DownloadState {
    object Idle : DownloadState
    data class Downloading(val progress: Int, val bytesDownloaded: Long, val totalBytes: Long) :
        DownloadState

    data class Success(val file: File) : DownloadState
    data class Error(val message: String) : DownloadState
}