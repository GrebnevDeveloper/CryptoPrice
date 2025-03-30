package com.grebnev.cryptoprice.presentation.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.ViewErrorScreenBinding

class ErrorScreenView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private var _binding: ViewErrorScreenBinding? = null
        private val binding: ViewErrorScreenBinding
            get() = _binding ?: throw RuntimeException("ViewErrorScreenBinding is null")

        init {
            _binding = ViewErrorScreenBinding.inflate(LayoutInflater.from(context), this)

            context.obtainStyledAttributes(attrs, R.styleable.ErrorScreenView).apply {
                try {
                    with(binding) {
                        errorMessage.text = getString(R.styleable.ErrorScreenView_errorMessage)
                        retry.text = getString(R.styleable.ErrorScreenView_retryButtonText)

                        val isVisibleRetry = getBoolean(R.styleable.ErrorScreenView_showRetryButton, false)
                        if (isVisibleRetry) {
                            loadingIndicator.visibility = GONE
                            retry.visibility = VISIBLE
                        } else {
                            retry.visibility = GONE
                            loadingIndicator.visibility = VISIBLE
                        }
                    }
                } finally {
                    recycle()
                }
            }
        }

        fun setErrorMessage(message: String) {
            binding.errorMessage.text = message
        }

        fun setOnRetryListener(listener: OnClickListener) {
            binding.retry.setOnClickListener(listener)
        }
    }