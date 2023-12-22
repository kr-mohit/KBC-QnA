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
        exoplayerViewModel.getMusicSharedPref()
        exoplayerViewModel.getSfxAudioSharedPref()
    }

    private fun setObservers() {

        exoplayerViewModel.isMusicOn.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnMusic.text = getString(R.string.on)
                playMusic(MusicToPlay.HOME_SCREEN)
            } else {
                binding.btnMusic.text = getString(R.string.off)
                stopMusic()
            }
        }

        exoplayerViewModel.isSfxAudioOn.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnSfxAudio.text = getString(R.string.on)
                playSfxAudio()
            } else {
                binding.btnSfxAudio.text = getString(R.string.off)
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

        binding.btnMusic.setOnClickListenerWithSfxAudio {
            exoplayerViewModel.setMusicOnOff()
        }

        binding.btnSfxAudio.setOnClickListener {
            exoplayerViewModel.setSfxAudioOnOff()
        }

        binding.btnRegion.setOnClickListenerWithSfxAudio {
            showComingSoonToast()
//            settingViewModel.onRegionClicked()
        }

        binding.btnBack.setOnClickListenerWithSfxAudio {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}