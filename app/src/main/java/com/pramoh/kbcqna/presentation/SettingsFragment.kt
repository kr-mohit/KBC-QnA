package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingViewModel: SettingsViewModel by viewModels()
    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

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
        settingViewModel.getRegionSharedPref()
        exoplayerViewModel.getAudioSharedPref()
    }

    private fun setObservers() {

        exoplayerViewModel.isSoundOn.observe(viewLifecycleOwner) {

            if (it) {
                binding.btnSound.text = getString(R.string.on)
                exoplayerViewModel.setupAndPlay(R.raw.audio_home_screen)
            } else {
                binding.btnSound.text = getString(R.string.off)
                exoplayerViewModel.stop()
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
            showComingSoonToast()
            exoplayerViewModel.setSoundOnOff()
        }

        binding.btnRegion.setOnClickListener {
            showComingSoonToast()
//            settingViewModel.onRegionClicked()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}