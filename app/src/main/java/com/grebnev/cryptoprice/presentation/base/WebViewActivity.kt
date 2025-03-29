package com.grebnev.cryptoprice.presentation.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.grebnev.cryptoprice.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityWebViewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        finish()
                    }
                }
            },
        )

        val url =
            intent.getStringExtra(URL_KEY) ?: run {
                finish()
                return
            }
        setupWebView(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webChromeClient =
                object : WebChromeClient() {
                    override fun onProgressChanged(
                        view: WebView?,
                        newProgress: Int,
                    ) {
                        binding.pbLoadingIndicator.apply {
                            when {
                                newProgress < 100 -> {
                                    visibility = View.VISIBLE
                                    setProgressCompat(newProgress, true)
                                }
                                else -> {
                                    setProgressCompat(100, true)
                                    postDelayed({ visibility = View.GONE }, 200)
                                }
                            }
                        }
                    }

                    override fun onReceivedTitle(
                        view: WebView?,
                        title: String?,
                    ) {
                        super.onReceivedTitle(view, title)
                        title?.takeIf { it.isNotBlank() }?.let {
                            binding.toolbar.title = it
                        }
                    }
                }

            webViewClient =
                object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView?,
                        url: String?,
                    ) {
                        view?.title?.takeIf { it.isNotBlank() }?.let {
                            binding.toolbar.title = it
                        }
                    }
                }

            loadUrl(url)
        }
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val URL_KEY = "url"
    }
}