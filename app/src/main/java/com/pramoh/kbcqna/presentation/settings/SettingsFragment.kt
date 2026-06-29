package com.pramoh.kbcqna.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentSettingsBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.presentation.ExoplayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingViewModel: SettingsViewModel by viewModels()
    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

    private var developerClickCount = 0
    private var lastDeveloperClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSavedData()
        setObservers()
        setOnClickListeners()
        setAppVersionText()
        setupDeveloperModeClick()
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

    private fun setAppVersionText() {
        val appName = getString(R.string.app_name)
        val version = try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        binding.tvAppVersion.text = getString(R.string.app_version, appName, version)
    }

    private fun setupDeveloperModeClick() {
        binding.tvAppVersion.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDeveloperClickTime < 2000) {
                developerClickCount++
            } else {
                developerClickCount = 1
            }
            lastDeveloperClickTime = currentTime

            if (developerClickCount >= 15) {
                developerClickCount = 0
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAdminFragment())
            }
        }
    }
}