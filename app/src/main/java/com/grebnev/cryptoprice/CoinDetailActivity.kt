package com.grebnev.cryptoprice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.grebnev.cryptoprice.databinding.ActivityCoinDetailBinding
import com.squareup.picasso.Picasso

class CoinDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoinDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fSym = intent.getStringExtra(EXTRA_FROM_SYMBOL)
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        fSym?.let {
            viewModel.getDetailInfo(it).observe(this) { coin ->
                binding.tvFromSymbol.text = coin.fromSymbol
                binding.tvToSymbol.text = coin.toSymbol
                binding.tvPrice.text = coin.price.toString()
                binding.tvMinPrice.text = coin.lowDay.toString()
                binding.tvMaxPrice.text = coin.highDay.toString()
                binding.tvLastMarket.text = coin.lastMarket
                binding.tvLastUpdate.text = coin.getFormattedTime()
                Picasso.get().load(coin.getFullUrlImage()).into(binding.ivLogoCoinDetail)
            }
        }
    }

    companion object {
        private const val EXTRA_FROM_SYMBOL = "from_symbol"

        fun newIntent(context: Context, fromSymbol: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(EXTRA_FROM_SYMBOL, fromSymbol)
            return intent
        }
    }
}