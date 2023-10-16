package com.pramoh.kbcqna.presentation

import androidx.fragment.app.Fragment
import com.pramoh.kbcqna.R

open class BaseFragment: Fragment() {

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    fun gotoFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fl_fragment_container, fragment)
            addToBackStack(null) // TODO: remove this
            commit()
        }
    }
}