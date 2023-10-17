package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSavedData()
        setObservers()
        setOnClickListeners()
    }

    private fun getSavedData() {
        settingViewModel.getDataFromSharedPref()
    }

    private fun setObservers() {

        settingViewModel.isSoundOn.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnSound.text = getString(R.string.on)
            } else {
                binding.btnSound.text = getString(R.string.off)
            }
        }

        settingViewModel.isRegionIndia.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnRegion.text = getString(R.string.india)
            } else {
                binding.btnRegion.text = getString(R.string.global)
            }
        }
    }

    private fun setOnClickListeners() {

        binding.btnSound.setOnClickListener {
            settingViewModel.onSoundClicked()
        }

        binding.btnRegion.setOnClickListener {
            settingViewModel.onRegionClicked()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}