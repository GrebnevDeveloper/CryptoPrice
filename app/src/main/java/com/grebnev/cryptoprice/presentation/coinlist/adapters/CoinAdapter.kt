package com.grebnev.cryptoprice.presentation.coinlist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.grebnev.core.extensions.formatWithRoundAndDelimiter
import com.grebnev.core.extensions.formatWithRoundAndSuffix
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
        val changePct24Template =
            context.resources.getString(R.string.change_pct_24_hour_template)
        val costTemplate =
            context.resources.getString(R.string.cost_template)
        with(holder.binding) {
            tvSymbols.text = coin.fromSymbol
            tvPrice.text =
                String.format(costTemplate, coin.price?.formatWithRoundAndDelimiter())
            Picasso.get().load(coin.imageUrl).into(ivLogoCoin)
            tvMktCap.text = String.format(costTemplate, coin.mktCap?.formatWithRoundAndSuffix())
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