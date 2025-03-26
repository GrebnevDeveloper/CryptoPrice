package com.grebnev.cryptoprice.presentation.coinitem.terminal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.grebnev.cryptoprice.databinding.FragmentTerminalBarsBinding
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.base.ViewModelFactory
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TerminalBarsState
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TerminalScreen
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TimeFrame
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
        val fromSymbol = requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
        displayTerminalBars(fromSymbol = fromSymbol)
    }

    private fun displayTerminalBars(
        timeFrame: TimeFrame = TimeFrame.DAILY,
        fromSymbol: String,
    ) {
        viewModel.loadBarsForCoin(timeFrame, fromSymbol)
        viewModel.barState.asLiveData().observe(viewLifecycleOwner) { terminalBarsState ->
            if (terminalBarsState is TerminalBarsState.Content) {
                val isVisibleCoinInfo = if (terminalBarsState.isFullScreen) View.GONE else View.VISIBLE
            }
            binding.composeViewTerminalBars.setContent {
                TerminalScreen(
                    modifier = Modifier,
                    terminalBarsState = terminalBarsState,
                    onRetryClickListener = {
                        viewModel.loadBarsForCoin(timeFrame, fromSymbol)
                    },
                    onTimeFrameSelected = { timeFrame ->
                        viewModel.changeTimeFrameStatus(timeFrame, fromSymbol)
                    },
                    onChangedStatusFullScreenListener = { viewModel.changeFullScreenStatus() },
                )
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

        fun newInstance(fromSymbol: String): TerminalBarsFragment =
            TerminalBarsFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(EXTRA_FROM_SYMBOL, fromSymbol)
                    }
            }
    }
}