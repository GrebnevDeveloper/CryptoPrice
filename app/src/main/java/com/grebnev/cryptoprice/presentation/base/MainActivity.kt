package com.grebnev.cryptoprice.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.ActivityMainBinding
import com.grebnev.cryptoprice.presentation.coinlist.CoinListFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, CoinListFragment.newInstance())
            .commit()
    }
}