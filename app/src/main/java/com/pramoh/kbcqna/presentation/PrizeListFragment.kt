package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentPrizeListBinding

class PrizeListFragment : BaseFragment() {

    private lateinit var binding: FragmentPrizeListBinding
    private lateinit var viewModel: PrizeListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prize_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PrizeListViewModel::class.java]

        setObservers()
        setListener()
    }

    private fun setObservers() {
        viewModel.listOfPrizes.observe(viewLifecycleOwner) {
            binding.listView.adapter = PrizeListAdapter(it, viewModel.currentQuestion)
            binding.listView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setListener() {
        binding.btnNext.setOnClickListener {
            gotoFragment(QuestionFragment())
        }
    }
}