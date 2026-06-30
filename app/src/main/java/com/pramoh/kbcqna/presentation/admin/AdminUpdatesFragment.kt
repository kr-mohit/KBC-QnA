package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminUpdatesBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminUpdatesFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminUpdatesBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private val dialogTypes = listOf("none", "soft", "hard")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_updates, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupObservers()
        setOnClickListeners()

        adminViewModel.loadAppUpdateConfig()
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, dialogTypes)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerDialogType.adapter = spinnerAdapter
    }

    private fun setupObservers() {
        adminViewModel.appUpdateInfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    response.data?.let { info ->
                        binding.etNewVersion.setText(info.newVersion)
                        binding.etUpdateMessage.setText(info.updateMessage)
                        val pos = dialogTypes.indexOf(info.dialogType)
                        if (pos >= 0) {
                            binding.spinnerDialogType.setSelection(pos)
                        }
                    }
                }
                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to load update settings", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adminViewModel.updateInfoStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = getString(R.string.updating_firebase_)
                }
                is Response.Success -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.update_firebase)
                    Toast.makeText(context, "App Update settings updated in Firebase!", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetUpdateInfoStatus()
                }
                is Response.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.update_firebase)
                    Toast.makeText(context, response.error ?: "Update failed", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetUpdateInfoStatus()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListenerWithSfxAudio {
            val version = binding.etNewVersion.text.toString().trim()
            val message = binding.etUpdateMessage.text.toString().trim()
            val selectedType = binding.spinnerDialogType.selectedItem.toString()

            if (version.isEmpty()) {
                Toast.makeText(context, "Version name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                adminViewModel.updateAppUpdateConfig(version, selectedType, message)
            }
        }
    }
}
