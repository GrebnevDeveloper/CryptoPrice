package com.grebnev.cryptoprice.presentation.coinitem.terminal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.key
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.databinding.FragmentTerminalBarsBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.Constants
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TerminalScreen
import javax.inject.Inject

class TerminalBarsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TerminalBarsViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as BaseApplication).component
    }

    private var _binding: FragmentTerminalBarsBinding? = null
    private val binding: FragmentTerminalBarsBinding
        get() = _binding ?: throw RuntimeException("FragmentTerminalBarsBinding is null")

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTerminalBarsBinding.inflate(inflater, container, false)
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
        displayTerminalBars(fromSymbol)
    }

    private fun displayTerminalBars(fromSymbol: String) {
        viewModel.loadBarsForCoin(fromSymbol)
        viewModel.barState.observe(viewLifecycleOwner) { screen ->
            when (screen) {
                is TerminalBarsScreenState.Content -> {
                    val currentTimeFrame = viewModel.timeFrame.value
                    binding.composeViewTerminalBars.setContent {
                        key(currentTimeFrame) {
                            TerminalScreen(
                                bars = screen.bars,
                                timeFrame = currentTimeFrame,
                                onTimeFrameSelected = { timeFrame ->
                                    viewModel.changeTimeFrameStatus(timeFrame, fromSymbol)
                                },
                            )
                        }
                    }
                    binding.composeViewTerminalBars.visibility = View.VISIBLE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.GONE
                }
                is TerminalBarsScreenState.Error -> {
                    binding.errorScreen.setErrorMessage(screen.message)
                    binding.errorScreen.setOnRetryListener {
                        viewModel.loadBarsForCoin(fromSymbol)
                    }
                    binding.composeViewTerminalBars.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.GONE
                    binding.errorScreen.visibility = View.VISIBLE
                }
                TerminalBarsScreenState.Loading -> {
                    binding.composeViewTerminalBars.visibility = View.GONE
                    binding.pbLoadingIndicator.visibility = View.VISIBLE
                    binding.errorScreen.visibility = View.GONE
                }
                TerminalBarsScreenState.Initial -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(fromSymbol: String): TerminalBarsFragment =
            TerminalBarsFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(Constants.Args.EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}