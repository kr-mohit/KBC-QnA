package com.pramoh.kbcqna.utils

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object AdminBypassUtil {

    private const val REQUIRED_CLICKS = 15
    private const val CLICK_TIME_WINDOW = 2000L // 2 seconds
    private var remoteAdminPasskey: String? = null

    /**
     * Sets the admin passkey fetched from Firebase.
     */
    fun setAdminPasskey(passkey: String?) {
        remoteAdminPasskey = passkey
    }

    /**
     * Attaches the click listener for the developer admin bypass.
     * Tracks click timing, triggers passcode prompt, and fires onSuccess when passcode is correct.
     */
    fun attachBypass(
        view: View,
        onSuccess: () -> Unit
    ) {
        var clickCount = 0
        var lastClickTime = 0L

        view.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < CLICK_TIME_WINDOW) {
                clickCount++
            } else {
                clickCount = 1
            }
            lastClickTime = currentTime

            if (clickCount >= REQUIRED_CLICKS) {
                clickCount = 0
                val context = view.context
                if (!NetworkUtils.isOnline(context)) {
                    Toast.makeText(context, "Internet connection required to bypass", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (remoteAdminPasskey.isNullOrBlank()) {
                    Toast.makeText(context, "Admin passkey not loaded from server", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                showPasscodeDialog(context, onSuccess)
            }
        }
    }

    /**
     * Shows the passcode input dialog.
     */
    private fun showPasscodeDialog(context: Context, onSuccess: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Admin PIN")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.hint = "Enter PIN"
        
        // Add padding around the text input for standard UI aesthetics
        val density = context.resources.displayMetrics.density
        val paddingDp = (20 * density).toInt()
        input.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)

        builder.setView(input)

        builder.setPositiveButton("Verify") { dialog, _ ->
            val enteredText = input.text.toString().trim()
            if (enteredText == remoteAdminPasskey) {
                dialog.dismiss()
                onSuccess()
            } else {
                Toast.makeText(context, "Invalid PIN", Toast.LENGTH_SHORT).show()
                showPasscodeDialog(context, onSuccess)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}
