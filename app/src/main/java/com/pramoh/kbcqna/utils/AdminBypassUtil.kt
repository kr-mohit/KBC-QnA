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
    private const val ADMIN_PASSCODE = "2911"

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
                showPasscodeDialog(view.context, onSuccess)
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
            if (enteredText == ADMIN_PASSCODE) {
                dialog.dismiss()
                onSuccess()
            } else {
                Toast.makeText(context, "Invalid PIN", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
