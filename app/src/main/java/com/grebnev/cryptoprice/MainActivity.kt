package com.grebnev.cryptoprice

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var coinViewModel: CoinViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coinViewModel = ViewModelProvider(this)[CoinViewModel::class.java]
//        coinViewModel.priceList.observe(this) {
//            Log.d("TEST_OF_LOADING_DATA", "Success in activity: $it")
//        }
        coinViewModel.getDetailInfo("BTC").observe(this) {
            Log.d("TEST_OF_LOADING_DATA", "Success in activity: $it")
        }
    }
}