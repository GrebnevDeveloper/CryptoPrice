package com.grebnev.cryptoprice.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.FragmentCoinListBinding
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.presentation.adapters.CoinAdapter

class CoinListFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this)[CoinListViewModel::class.java]
    }

    private var _binding: FragmentCoinListBinding? = null
    private val binding: FragmentCoinListBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinItemBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CoinAdapter(requireActivity())
        adapter.onCoinClickListener = object : CoinAdapter.OnCoinClickListener {
            override fun onCoinClick(coin: Coin) {
                if (isLandscapeOrientation()) {
                    requireActivity().supportFragmentManager.popBackStack()
                    launchCoinItemFragment(R.id.second_container, coin)
                } else {
                    launchCoinItemFragment(R.id.main_container, coin)
                }
            }
        }
        binding.rvCoinPriceList.adapter = adapter
        viewModel.coinList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun isLandscapeOrientation() =
        requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun launchCoinItemFragment(resId: Int, coin: Coin) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(resId, CoinItemFragment.newInstance(coin.fromSymbol))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): CoinListFragment {
            return CoinListFragment()
        }
    }
}