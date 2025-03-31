package com.grebnev.cryptoprice.presentation.base.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.grebnev.cryptoprice.R
import timber.log.Timber

class NetworkMonitor(
    private val context: Context,
    private val lifecycle: Lifecycle,
) : DefaultLifecycleObserver {
    private var snackbar: Snackbar? = null
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val networkCallback = createNetworkCallback()

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        registerNetworkCallback()
        checkInitialConnection()
    }

    override fun onStop(owner: LifecycleOwner) {
        unregisterNetworkCallback()
    }

    private fun checkInitialConnection() {
        val isConnected =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities =
                    network?.let {
                        connectivityManager.getNetworkCapabilities(network)
                    }
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
            } else {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.isConnected == true
            }

        if (!isConnected) {
            postToUiThread { showNoInternetSnackbar() }
        }
    }

    private fun registerNetworkCallback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request =
                    NetworkRequest
                        .Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()
                connectivityManager.registerNetworkCallback(request, networkCallback)
            }
        } catch (e: Exception) {
            Timber.e(e, "Network callback registration failed")
        }
    }

    private fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Network callback was not registered")
        }
        dismissSnackbar()
    }

    private fun createNetworkCallback() =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postToUiThread { dismissSnackbar() }
            }

            override fun onLost(network: Network) {
                postToUiThread { showNoInternetSnackbar() }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities,
            ) {
                val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                postToUiThread {
                    if (hasInternet) dismissSnackbar() else showNoInternetSnackbar()
                }
            }
        }

    private fun showNoInternetSnackbar() {
        val activity = context as? Activity ?: return
        if (activity.isFinishing || activity.isDestroyed) return
        if (snackbar?.isShown == true) return

        snackbar =
            Snackbar
                .make(
                    activity.findViewById(android.R.id.content),
                    context.getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_INDEFINITE,
                ).apply {
                    setTextColor(R.color.md_theme_error_highContrast)
                    show()
                }
    }

    private fun dismissSnackbar() {
        snackbar?.dismiss()
        snackbar = null
    }

    private fun postToUiThread(action: () -> Unit) {
        (context as? Activity)?.runOnUiThread(action)
    }

    fun dispose() {
        lifecycle.removeObserver(this)
        unregisterNetworkCallback()
    }
}