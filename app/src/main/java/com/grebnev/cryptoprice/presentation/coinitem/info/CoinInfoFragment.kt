package com.grebnev.cryptoprice.presentation.coinitem.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.grebnev.core.extensions.formatWithRoundAndDelimiter
import com.grebnev.core.extensions.formatWithRoundAndSuffix
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.FragmentCoinInfoBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.Constants
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
        val fromSymbol =
            requireArguments().getString(
                Constants.Args.EXTRA_FROM_SYMBOL,
                Constants.Args.EMPTY_SYMBOL,
            )
        viewModel.getCoinInfo(fromSymbol)
        displayCoinInfo()
    }

    private fun displayCoinInfo() {
        viewModel.screenState.observe(viewLifecycleOwner) { screen ->
            when (screen) {
                is CoinInfoScreenState.Error -> {
                    binding.errorScreen.setErrorMessage(screen.message)
                    binding.cvCoinInfo.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.VISIBLE
                }

                CoinInfoScreenState.Loading -> {
                    binding.cvCoinInfo.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.VISIBLE
                    binding.errorScreen.visibility = View.GONE
                }

                is CoinInfoScreenState.Content -> {
                    displayCoinInfoContent(screen)
                    binding.cvCoinInfo.visibility = View.VISIBLE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.GONE
                }

                CoinInfoScreenState.Initial -> {}
            }
        }
    }

    private fun displayCoinInfoContent(screen: CoinInfoScreenState.Content) {
        val changePctTemplate =
            requireContext().resources.getString(R.string.change_pct_24_hour_template)
        val costTemplate =
            requireContext().resources.getString(R.string.cost_template)
        with(binding) {
            with(screen) {
                tvFromSymbol.text = coin.fromSymbol
                tvToSymbol.text = coin.toSymbol
                ciPrice.setValue(
                    String.format(
                        costTemplate,
                        coin.price?.formatWithRoundAndDelimiter(),
                    ),
                )
                ciOpenPrice.setValue(
                    String.format(
                        costTemplate,
                        coin.openDay?.formatWithRoundAndDelimiter(),
                    ),
                )
                ciMinPrice.setValue(
                    String.format(
                        costTemplate,
                        coin.lowDay?.formatWithRoundAndDelimiter(),
                    ),
                )
                ciMaxPrice.setValue(
                    String.format(
                        costTemplate,
                        coin.highDay?.formatWithRoundAndDelimiter(),
                    ),
                )
                ciChangePctDay.setValue(
                    String.format(
                        changePctTemplate,
                        coin.changePctDay,
                    ),
                )
                ciVolumeDay.setValue(
                    String.format(
                        costTemplate,
                        coin.volumeDay?.formatWithRoundAndDelimiter()
                            ?: getString(R.string.no_data),
                    ),
                )
                ciMktCap.setValue(
                    String.format(
                        costTemplate,
                        coin.mktCap?.formatWithRoundAndSuffix()
                            ?: getString(R.string.no_data),
                    ),
                )
                ciLastMarket.setValue(coin.lastMarket ?: getString(R.string.no_data))
                ciLastUpdate.setValue(coin.lastUpdate)
                ciLastUpdate.showDivider(false)
                Picasso.get().load(coin.imageUrl).into(ivLogoCoinDetail)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(fromSymbol: String): CoinInfoFragment =
            CoinInfoFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(Constants.Args.EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}