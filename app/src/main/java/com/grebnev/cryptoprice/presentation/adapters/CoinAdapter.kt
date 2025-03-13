package com.grebnev.cryptoprice.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.color.MaterialColors.getColor
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.databinding.ItemCoinInfoBinding
import com.grebnev.cryptoprice.domain.entity.Coin
import com.squareup.picasso.Picasso

class CoinAdapter(
    private val context: Context,
) : ListAdapter<Coin, CoinViewHolder>(CoinDiffCallback) {
    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CoinViewHolder {
        val binding =
            ItemCoinInfoBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CoinViewHolder,
        position: Int,
    ) {
        val coin = getItem(position)
        val symbolsTemplate =
            context.resources.getString(com.grebnev.cryptoprice.R.string.symbols_template)
        val lastUpdateTemplate =
            context.resources.getString(com.grebnev.cryptoprice.R.string.last_update_template)
        val changePct24Template =
            context.resources.getString(R.string.change_pct_24_hour_template)
        val priceTemplate =
            context.resources.getString(R.string.price_template)
        val mktCapTemplate =
            context.resources.getString(R.string.mktcap_template)
        with(holder.binding) {
            tvSymbols.text = coin.fromSymbol
            tvPrice.text =
                String.format(priceTemplate, coin.price.toString())
            Picasso.get().load(coin.imageUrl).into(ivLogoCoin)
            tvMktCap.text = String.format(mktCapTemplate, coin.mktCap?.toInt())
            tvChangePct24.text = String.format(changePct24Template, coin.changePct24Hour)
            tvChangePct24.setTextColor(
                ContextCompat.getColor(context, getColorForPct24Hour(coin.changePct24Hour)),
            )
        }
        holder.itemView.setOnClickListener {
            onCoinClickListener?.onCoinClick(coin)
        }
    }

    private fun getColorForPct24Hour(changePct24Hour: Double?): Int {
        if (changePct24Hour != null) {
            if (changePct24Hour > 0) {
                return R.color.max_price
            }
        }
        return R.color.min_price
    }

    interface OnCoinClickListener {
        fun onCoinClick(coin: Coin)
    }
}