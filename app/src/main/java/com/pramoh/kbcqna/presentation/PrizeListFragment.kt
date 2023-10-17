package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentPrizeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrizeListFragment : BaseFragment() {

    private lateinit var binding: FragmentPrizeListBinding
    private val prizeListViewModel: PrizeListViewModel by viewModels()
    private val args: PrizeListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prize_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setOnClickListeners()
    }

    private fun setObservers() {
        prizeListViewModel.listOfPrizes.observe(viewLifecycleOwner) {
            binding.listView.adapter = PrizeListAdapter(it, args.questionToBeAsked)
            binding.listView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setOnClickListeners() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(PrizeListFragmentDirections.actionPrizeListFragmentToQuestionFragment(args.questionToBeAsked))
        }
    }
}