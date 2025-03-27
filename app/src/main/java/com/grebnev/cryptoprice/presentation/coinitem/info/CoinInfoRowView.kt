package com.grebnev.cryptoprice.presentation.coinitem.info

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.grebnev.cryptoprice.R

class CoinInfoRowView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val labelView: MaterialTextView
        private val valueView: MaterialTextView
        private val divider: View

        init {
            LayoutInflater.from(context).inflate(R.layout.view_coin_info_row, this, true)

            labelView = findViewById(R.id.label)
            valueView = findViewById(R.id.value)
            divider = findViewById(R.id.divider)

            context.obtainStyledAttributes(attrs, R.styleable.CoinInfoRowView).apply {
                try {
                    labelView.text = getString(R.styleable.CoinInfoRowView_labelText)
                    valueView.text = getString(R.styleable.CoinInfoRowView_valueText)

                    @ColorInt val color = getColor(R.styleable.CoinInfoRowView_valueColor, Color.TRANSPARENT)
                    if (color != Color.TRANSPARENT) {
                        valueView.setTextColor(color)
                    }

                    divider.visibility =
                        if (getBoolean(R.styleable.CoinInfoRowView_showDivider, true)) {
                            VISIBLE
                        } else {
                            GONE
                        }
                } finally {
                    recycle()
                }
            }
        }

        fun setLabel(text: String) {
            labelView.text = text
        }

        fun setValue(text: String) {
            valueView.text = text
        }

        fun setValueColor(
            @ColorInt color: Int,
        ) {
            valueView.setTextColor(color)
        }

        fun showDivider(show: Boolean) {
            divider.visibility = if (show) VISIBLE else GONE
        }
    }