package com.pramoh.kbcqna.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentWinningBinding

class WinningFragment : Fragment() {

    private lateinit var binding: FragmentWinningBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_winning, container, false)
    return binding.root
}
}