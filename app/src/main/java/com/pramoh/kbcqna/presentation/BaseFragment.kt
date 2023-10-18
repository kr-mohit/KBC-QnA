package com.pramoh.kbcqna.presentation

import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    fun showComingSoonToast() {
        Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT).show()
    }
}