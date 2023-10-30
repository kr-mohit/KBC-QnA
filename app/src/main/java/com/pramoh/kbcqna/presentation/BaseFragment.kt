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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.pramoh.kbcqna.R


open class BaseFragment: Fragment() {

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
                positiveButtonAction?.invoke()
                dialog.dismiss()
            }
        }
        negativeButton.apply {
            text = negativeButtonText
            setOnClickListener {
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
}