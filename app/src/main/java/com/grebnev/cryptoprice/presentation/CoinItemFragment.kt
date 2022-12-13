package com.grebnev.cryptoprice.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.databinding.FragmentCoinItemBinding
import com.squareup.picasso.Picasso

class CoinItemFragment : Fragment() {
    private val binding by lazy {
        FragmentCoinItemBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, defaultViewModelProviderFactory)[CoinItemViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCoinItem().observe(viewLifecycleOwner) {
            with(binding) {
                tvFromSymbol.text = it.fromSymbol
                tvToSymbol.text = it.toSymbol
                tvPrice.text = it.price.toString()
                tvMinPrice.text = it.lowDay.toString()
                tvMaxPrice.text = it.highDay.toString()
                tvLastMarket.text = it.lastMarket
                tvLastUpdate.text = it.getFormattedTime()
                Picasso.get().load(it.getFullUrlImage()).into(binding.ivLogoCoinDetail)
            }
        }

    }
}