package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminMaintenanceBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMaintenanceFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminMaintenanceBinding
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_maintenance, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setOnClickListeners()

        adminViewModel.loadAppUpdateConfig()
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
                        binding.switchMaintenance.isChecked = info.isMaintenanceMode
                        binding.etMaintenanceMessage.setText(info.maintenanceMessage)
                    }
                }
                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to load maintenance settings", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adminViewModel.maintenanceUpdateStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = "Updating Firebase..."
                }
                is Response.Success -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Update Firebase"
                    Toast.makeText(context, "Maintenance Mode updated in Firebase!", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetMaintenanceUpdateStatus()
                }
                is Response.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Update Firebase"
                    Toast.makeText(context, response.error ?: "Update failed", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetMaintenanceUpdateStatus()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListenerWithSfxAudio {
            val isMaintenance = binding.switchMaintenance.isChecked
            val message = binding.etMaintenanceMessage.text.toString().trim()
            adminViewModel.updateMaintenance(isMaintenance, message)
        }
    }
}
