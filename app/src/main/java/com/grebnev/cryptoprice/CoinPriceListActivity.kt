package com.grebnev.cryptoprice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.data.api.model.CoinDto
import com.grebnev.cryptoprice.databinding.ActivityCoinPriceListBinding

class CoinPriceListActivity : AppCompatActivity() {

    private lateinit var coinViewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoinPriceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = CoinPriceAdapter(this)
        binding.rvCoinPriceList.adapter = adapter
        coinViewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        coinViewModel.priceList.observe(this) {
            adapter.coinDtoList = it
        }
        adapter.onCoinClickListener = object : CoinPriceAdapter.OnCoinClickListener {
            override fun onCoinClick(coin: CoinDto) {
                val intent =
                    CoinDetailActivity.newIntent(this@CoinPriceListActivity, coin.fromSymbol)
                startActivity(intent)
            }

        }
    }
}