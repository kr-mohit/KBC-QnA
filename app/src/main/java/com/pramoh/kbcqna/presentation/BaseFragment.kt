package com.pramoh.kbcqna.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.pramoh.kbcqna.R

open class BaseFragment: Fragment() {

    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

    fun showComingSoonToast() {
        Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT).show()
    }

    fun showDialog(
        context: Context,
        titleText: String,
        negativeButtonText: String,
        positiveButtonText: String? = null,
        positiveButtonAction: (() -> Unit)? = null
    ) {

        val dialog = AlertDialog.Builder(context).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_box, null)
        val textView = dialogView.findViewById<TextView>(R.id.tv_popup_window_text)
        val positiveButton = dialogView.findViewById<Button>(R.id.btn_popup_window_button_1)
        val negativeButton = dialogView.findViewById<Button>(R.id.btn_popup_window_button_2)

        textView.text = titleText
        positiveButton.apply {
            visibility = if (positiveButtonText != null) View.VISIBLE else View.GONE
            text = positiveButtonText
            setOnClickListener {
                playSfxAudio()
                positiveButtonAction?.invoke()
                dialog.dismiss()
            }
        }
        negativeButton.apply {
            text = negativeButtonText
            setOnClickListener {
                playSfxAudio()
                dialog.dismiss()
            }
        }

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog.window?.insetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            dialog.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    @OptIn(UnstableApi::class)
    fun playMusic(musicToPlay: MusicToPlay, ticktock: Boolean = true) {
        if (exoplayerViewModel.isMusicOn.value == true) {
            when(musicToPlay) {
                MusicToPlay.HOME_SCREEN -> {
                    val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(musicToPlay.source))
                    if (exoplayerViewModel.musicPlayer.currentMediaItem != mediaItem) {
                        exoplayerViewModel.playMusic(musicToPlay.source, true)
                    }
                }
                MusicToPlay.PRIZE_LIST,
                MusicToPlay.SUSPENSE,
                MusicToPlay.RESULT_SCREEN -> exoplayerViewModel.playMusic(musicToPlay.source, true)
                MusicToPlay.QUESTIONNAIRE -> {
                    exoplayerViewModel.playMusic(musicToPlay.source, false)
                    if (ticktock) setQuestionnaireTransition(MusicToPlay.TICKTOCK) else setQuestionnaireTransition(MusicToPlay.SUSPENSE)
                }
                else -> {exoplayerViewModel.playMusic(musicToPlay.source, false)}
            }
        }
    }

    fun stopMusic() {
        exoplayerViewModel.stopMusic()
    }

    fun playSfxAudio() {
        if (exoplayerViewModel.isSfxAudioOn.value == true) {
            exoplayerViewModel.playSfxAudio()
        }
    }

    private fun setQuestionnaireTransition(musicToPlay: MusicToPlay) {
        exoplayerViewModel.musicPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    playMusic(musicToPlay)
                }
            }
        })
    }

    enum class MusicToPlay(val source: Int) {
        HOME_SCREEN(R.raw.audio_home_screen),
        PRIZE_LIST(R.raw.audio_prize_list),
        QUESTIONNAIRE(R.raw.audio_questionnaire),
        TICKTOCK(R.raw.audio_ticktock),
        SUSPENSE(R.raw.audio_suspense),
        CORRECT_ANSWER(R.raw.audio_correct_answer),
        WRONG_ANSWER(R.raw.audio_wrong_answer),
        RESULT_SCREEN(R.raw.audio_result_screen)
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.setOnClickListenerWithSfxAudio(onClickAction: () -> Unit) {
        setOnClickListener {
            onClickAction.invoke()
            playSfxAudio()
        }
    }
}