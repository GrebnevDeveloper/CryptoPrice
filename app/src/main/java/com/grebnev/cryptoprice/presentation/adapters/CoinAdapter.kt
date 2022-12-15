package com.grebnev.cryptoprice.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.grebnev.cryptoprice.databinding.ItemCoinInfoBinding
import com.grebnev.cryptoprice.domain.entity.Coin
import com.squareup.picasso.Picasso

class CoinAdapter(private val context: Context) :
    ListAdapter<Coin, CoinViewHolder>(CoinDiffCallback) {
    var coinList: List<Coin> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = ItemCoinInfoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = getItem(position)
        val symbolsTemplate =
            context.resources.getString(com.grebnev.cryptoprice.R.string.symbols_template)
        val lastUpdateTemplate =
            context.resources.getString(com.grebnev.cryptoprice.R.string.last_update_template)
        with(holder.binding) {
            tvSymbols.text = String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
            tvPrice.text = coin.price.toString()
            tvLastUpdate.text = String.format(lastUpdateTemplate, coin.lastUpdate)
            Picasso.get().load(coin.imageUrl).into(ivLogoCoin)
        }
        holder.itemView.setOnClickListener {
            onCoinClickListener?.onCoinClick(coin)
        }
    }

    interface OnCoinClickListener {
        fun onCoinClick(coin: Coin)
    }

}