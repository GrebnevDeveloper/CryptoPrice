package com.grebnev.cryptoprice.presentation.coinlist

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.FragmentCoinListBinding
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.grebnev.cryptoprice.presentation.coinitem.pager.CoinItemFragment
import com.grebnev.cryptoprice.presentation.coinlist.adapter.CoinAdapter
import javax.inject.Inject

class CoinListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CoinListViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as BaseApplication).component
    }

    private var _binding: FragmentCoinListBinding? = null
    private val binding: FragmentCoinListBinding
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
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.screenState.asLiveData().observe(viewLifecycleOwner) { screen ->
            when (screen) {
                is CoinListScreenState.Error -> {
                    binding.errorScreen.setErrorMessage(screen.message)
                    binding.rvCoinPriceList.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.VISIBLE
                }

                CoinListScreenState.Initial -> {
                }

                CoinListScreenState.Loading -> {
                    binding.rvCoinPriceList.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.VISIBLE
                    binding.errorScreen.visibility = View.GONE
                }

                is CoinListScreenState.Success -> {
                    binding.tvTimeLastUpdate.text = screen.timeLastUpdate
                    val adapter = CoinAdapter(requireActivity())
                    adapter.onCoinClickListener =
                        object : CoinAdapter.OnCoinClickListener {
                            override fun onCoinClick(coin: Coin) {
                                if (isLandscapeOrientation()) {
                                    requireActivity().supportFragmentManager.popBackStack()
                                    launchCoinItemFragment(R.id.second_container, coin)
                                } else {
                                    launchCoinItemFragment(R.id.main_container, coin)
                                }
                            }
                        }
                    val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let { drawable ->
                        divider.setDrawable(drawable)
                    } ?: run {
                        divider.setDrawable(
                            ContextCompat
                                .getColor(
                                    requireContext(),
                                    R.color.md_theme_secondary,
                                ).toDrawable(),
                        )
                    }
                    binding.rvCoinPriceList.addItemDecoration(divider)
                    binding.rvCoinPriceList.adapter = adapter
                    adapter.submitList(screen.coins)
                    binding.rvCoinPriceList.visibility = View.VISIBLE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.GONE
                }
            }
        }
    }

    private fun isLandscapeOrientation() =
        requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun launchCoinItemFragment(
        resId: Int,
        coin: Coin,
    ) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(resId, CoinItemFragment.newInstance(coin.fromSymbol))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): CoinListFragment = CoinListFragment()
    }
}