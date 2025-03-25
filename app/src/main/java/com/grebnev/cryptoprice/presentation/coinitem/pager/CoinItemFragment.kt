package com.grebnev.cryptoprice.presentation.coinitem.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.FragmentCoinItemBinding
import com.grebnev.cryptoprice.presentation.coinitem.info.CoinInfoFragment
import com.grebnev.cryptoprice.presentation.coinitem.news.CoinNewsFragment
import com.grebnev.cryptoprice.presentation.coinitem.terminal.TerminalBarsFragment

class CoinItemFragment : Fragment() {
    private var _binding: FragmentCoinItemBinding? = null
    private val binding: FragmentCoinItemBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinItemBinding is null")

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

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(CoinInfoFragment.newInstance(fromSymbol), getString(R.string.page_info))
        adapter.addFragment(TerminalBarsFragment.newInstance(fromSymbol), getString(R.string.page_terminal))
        adapter.addFragment(CoinNewsFragment.newInstance(fromSymbol), getString(R.string.page_news))

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
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