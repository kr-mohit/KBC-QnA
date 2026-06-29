package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminPrizesBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPrizesFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminPrizesBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private var adapter: AdminPrizeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_prizes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setOnClickListeners()

        adminViewModel.loadPrizeConfig()
    }

    private fun setupObservers() {
        adminViewModel.prizeOptions.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.rvPrizeOptions.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.rvPrizeOptions.visibility = View.VISIBLE
                    response.data?.let { options ->
                        adapter = AdminPrizeAdapter(options)
                        binding.rvPrizeOptions.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvPrizeOptions.adapter = adapter
                    }
                }
                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to load options", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adminViewModel.updateStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = getString(R.string.updating_firebase_)
                }
                is Response.Success -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.update_firebase)
                    Toast.makeText(context, "Firebase updated successfully!", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetUpdateStatus()
                }
                is Response.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.update_firebase)
                    Toast.makeText(context, response.error ?: "Update failed", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetUpdateStatus()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnSelectAll.setOnClickListenerWithSfxAudio {
            adapter?.selectAll()
        }

        binding.btnUnselectAll.setOnClickListenerWithSfxAudio {
            adapter?.unselectAll()
        }

        binding.btnSave.setOnClickListenerWithSfxAudio {
            val selected = adapter?.getSelectedPrizes()
            if (selected.isNullOrEmpty()) {
                Toast.makeText(context, "Please select at least one prize amount", Toast.LENGTH_SHORT).show()
            } else {
                adminViewModel.updateRemotePrizes(selected)
            }
        }
    }
}
