package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnOptionPrizes.setOnClickListenerWithSfxAudio {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminPrizesFragment())
        }

        binding.btnOptionUpdates.setOnClickListenerWithSfxAudio {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminUpdatesFragment())
        }

        binding.btnOptionLeaderboard.setOnClickListenerWithSfxAudio {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminLeaderboardFragment())
        }

        binding.btnOptionStats.setOnClickListenerWithSfxAudio {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminStatsFragment())
        }

        binding.btnOptionMaintenance.setOnClickListenerWithSfxAudio {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminMaintenanceFragment())
        }
    }
}
