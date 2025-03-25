package com.grebnev.cryptoprice.presentation.coinitem.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.grebnev.cryptoprice.databinding.FragmentCoinInfoBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CoinInfoFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CoinInfoViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as BaseApplication).component
    }

    private var _binding: FragmentCoinInfoBinding? = null
    private val binding: FragmentCoinInfoBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinInfoBinding is null")

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCoinInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val fromSymbol = requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
        displayCoinInfo(fromSymbol)
    }

    private fun displayCoinInfo(fromSymbol: String) {
        viewModel.getCoinInfo(fromSymbol)
        viewModel.screenState.asLiveData().observe(viewLifecycleOwner) { screen ->
            when (screen) {
                is CoinInfoScreenState.Error -> {
                }
                CoinInfoScreenState.Initial -> {
                }
                CoinInfoScreenState.Loading -> {
                }
                is CoinInfoScreenState.Content -> {
                    with(binding) {
                        tvFromSymbol.text = screen.coin.fromSymbol
                        tvToSymbol.text = screen.coin.toSymbol
                        tvPrice.text = screen.coin.price.toString()
                        tvMinPrice.text = screen.coin.lowDay.toString()
                        tvMaxPrice.text = screen.coin.highDay.toString()
                        tvLastMarket.text = screen.coin.lastMarket
                        tvLastUpdate.text = screen.coin.lastUpdate
                        Picasso.get().load(screen.coin.imageUrl).into(binding.ivLogoCoinDetail)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EXTRA_FROM_SYMBOL = "from_symbol"
        private const val EMPTY_SYMBOL = ""

        fun newInstance(fromSymbol: String): CoinInfoFragment =
            CoinInfoFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}