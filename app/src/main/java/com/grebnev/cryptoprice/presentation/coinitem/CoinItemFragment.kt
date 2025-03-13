package com.grebnev.cryptoprice.presentation.coinitem

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.databinding.FragmentCoinItemBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CoinItemFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CoinItemViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as BaseApplication).component
    }

    private var _binding: FragmentCoinItemBinding? = null
    private val binding: FragmentCoinItemBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinItemBinding is null")

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCoinItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val fromSymbol = requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
        viewModel.getCoinItem(fromSymbol)
        viewModel.coinItem.observe(viewLifecycleOwner) {
            with(binding) {
                tvFromSymbol.text = it.fromSymbol
                tvToSymbol.text = it.toSymbol
                tvPrice.text = it.price.toString()
                tvMinPrice.text = it.lowDay.toString()
                tvMaxPrice.text = it.highDay.toString()
                tvLastMarket.text = it.lastMarket
                tvLastUpdate.text = it.lastUpdate
                Picasso.get().load(it.imageUrl).into(binding.ivLogoCoinDetail)
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

        fun newInstance(fromSymbol: String): CoinItemFragment =
            CoinItemFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}