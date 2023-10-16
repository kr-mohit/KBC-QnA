package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setObservers()
        setListeners()
    }

    private fun setObservers() {

        viewModel.isSoundOn.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnSound.text = getString(R.string.on)
            } else {
                binding.btnSound.text = getString(R.string.off)
            }
        }

        viewModel.isRegionIndia.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnRegion.text = getString(R.string.india)
            } else {
                binding.btnRegion.text = getString(R.string.global)
            }
        }
    }

    private fun setListeners() {

        binding.btnSound.setOnClickListener {
            viewModel.onSoundClicked()
        }

        binding.btnRegion.setOnClickListener {
            viewModel.onRegionClicked()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}