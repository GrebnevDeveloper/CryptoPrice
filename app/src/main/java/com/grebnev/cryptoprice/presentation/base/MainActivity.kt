package com.grebnev.cryptoprice.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.ActivityMainBinding
import com.grebnev.cryptoprice.presentation.base.utils.NetworkMonitor
import com.grebnev.cryptoprice.presentation.coinlist.CoinListFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        networkMonitor = NetworkMonitor(this, lifecycle)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, CoinListFragment.newInstance())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.dispose()
    }
}