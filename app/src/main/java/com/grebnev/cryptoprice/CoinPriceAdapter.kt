package com.grebnev.cryptoprice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grebnev.cryptoprice.data.pojo.price.CoinPrice
import com.grebnev.cryptoprice.databinding.ItemCoinInfoBinding
import com.squareup.picasso.Picasso

class CoinPriceAdapter(private val context: Context) :
    RecyclerView.Adapter<CoinPriceAdapter.CoinPriceViewHolder>() {
    var coinPriceList: List<CoinPrice> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinPriceViewHolder {
        val binding = ItemCoinInfoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinPriceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinPriceViewHolder, position: Int) {
        val coin = coinPriceList[position]
        holder.bind(coin)
        holder.itemView.setOnClickListener {
            onCoinClickListener?.onCoinClick(coin)
        }
    }

    override fun getItemCount() = coinPriceList.size

    inner class CoinPriceViewHolder(private val binding: ItemCoinInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coin: CoinPrice) {
            val symbolsTemplate = context.resources.getString(R.string.symbols_template)
            val lastUpdateTemplate = context.resources.getString(R.string.last_update_template)
            binding.tvSymbols.text = String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
            binding.tvPrice.text = coin.price.toString()
            binding.tvLastUpdate.text = String.format(lastUpdateTemplate, coin.getFormattedTime())
            Picasso.get().load(coin.getFullUrlImage()).into(binding.ivLogoCoin)
        }
    }

    interface OnCoinClickListener {
        fun onCoinClick(coin: CoinPrice)
    }

}