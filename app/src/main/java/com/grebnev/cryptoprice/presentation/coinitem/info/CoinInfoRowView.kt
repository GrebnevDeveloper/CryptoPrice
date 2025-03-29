package com.grebnev.cryptoprice.presentation.coinitem.info

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.ViewCoinInfoRowBinding

class CoinInfoRowView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private var _binding: ViewCoinInfoRowBinding? = null
        private val binding: ViewCoinInfoRowBinding
            get() = _binding ?: throw RuntimeException("ViewCoinInfoRowBinding is null")

        init {
            _binding = ViewCoinInfoRowBinding.inflate(LayoutInflater.from(context), this)

            context.obtainStyledAttributes(attrs, R.styleable.CoinInfoRowView).apply {
                try {
                    with(binding) {
                        label.text = getString(R.styleable.CoinInfoRowView_labelText)
                        value.text = getString(R.styleable.CoinInfoRowView_valueText)

                        @ColorInt val color =
                            getColor(R.styleable.CoinInfoRowView_valueColor, Color.TRANSPARENT)
                        if (color != Color.TRANSPARENT) {
                            value.setTextColor(color)
                        }

                        divider.visibility =
                            if (getBoolean(R.styleable.CoinInfoRowView_showDivider, true)) {
                                VISIBLE
                            } else {
                                GONE
                            }
                    }
                } finally {
                    recycle()
                }
            }
        }

        fun setLabel(text: String) {
            binding.label.text = text
        }

        fun setValue(text: String) {
            binding.value.text = text
        }

        fun setValueColor(
            @ColorInt color: Int,
        ) {
            binding.value.setTextColor(color)
        }

        fun showDivider(show: Boolean) {
            binding.divider.visibility = if (show) VISIBLE else GONE
        }
    }