package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentLevelsBinding
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LevelsFragment : BaseFragment() {

    private lateinit var binding: FragmentLevelsBinding
    private val levelsViewModel: LevelsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_levels, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setonClickListeners()
        setObservers()
    }

    private fun setonClickListeners() {

        with(binding) {
            btnLevelOne.setOnClickListenerWithSfxAudio { handleLevelClick(1) }
            btnLevelTwo.setOnClickListenerWithSfxAudio { handleLevelClick(2) }
            btnLevelThree.setOnClickListenerWithSfxAudio { handleLevelClick(3) }
            btnLevelFour.setOnClickListenerWithSfxAudio { handleLevelClick(4) }
            btnLevelFive.setOnClickListenerWithSfxAudio { handleLevelClick(5) }
            btnLevelSix.setOnClickListenerWithSfxAudio { handleLevelClick(6) }
            btnLevelSeven.setOnClickListenerWithSfxAudio { handleLevelClick(7) }
            btnLevelEight.setOnClickListenerWithSfxAudio { handleLevelClick(8) }

            btnBack.setOnClickListenerWithSfxAudio {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun handleLevelClick(level: Int) {
        levelsViewModel.getQuestionsForLevel(level)
    }

    private fun setObservers() {
        levelsViewModel.questionsFromDB.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Error -> Toast.makeText(context, "Error while getting Offline Questions.", Toast.LENGTH_SHORT).show()
                is Response.Loading -> Toast.makeText(context, "Getting offline question...", Toast.LENGTH_SHORT).show()
                is Response.Success -> {
                    it.data?.let { list ->
                        Toast.makeText(context, "Level: ${list[0].questionLevelId}, Questions: ${list.size}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}