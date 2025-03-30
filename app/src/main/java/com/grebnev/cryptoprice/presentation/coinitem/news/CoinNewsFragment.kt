package com.grebnev.cryptoprice.presentation.coinitem.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.grebnev.cryptoprice.databinding.FragmentCoinNewsBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.grebnev.cryptoprice.presentation.coinitem.news.adapter.NewsAdapter
import javax.inject.Inject

class CoinNewsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CoinNewsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as BaseApplication).component
    }

    private var _binding: FragmentCoinNewsBinding? = null
    private val binding: FragmentCoinNewsBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinNewsBinding is null")

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCoinNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val category = requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
        viewModel.getNewsForCoin(category)
        viewModel.screenState.asLiveData().observe(viewLifecycleOwner) { screen ->
            when (screen) {
                is CoinNewsScreenState.Content -> {
                    val adapter = NewsAdapter()
                    binding.rvCoinNewsList.adapter = adapter
                    adapter.submitList(screen.news)
                    binding.rvCoinNewsList.visibility = View.VISIBLE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.GONE
                }
                is CoinNewsScreenState.Error -> {
                    binding.errorScreen.setErrorMessage(screen.message)
                    binding.errorScreen.setOnRetryListener {
                        viewModel.getNewsForCoin(category)
                    }
                    binding.rvCoinNewsList.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.VISIBLE
                }
                CoinNewsScreenState.Initial -> {
                }
                CoinNewsScreenState.Loading -> {
                    binding.rvCoinNewsList.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.VISIBLE
                    binding.errorScreen.visibility = View.GONE
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

        fun newInstance(fromSymbol: String): CoinNewsFragment =
            CoinNewsFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}