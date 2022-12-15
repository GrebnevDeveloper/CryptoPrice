package com.grebnev.cryptoprice.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.grebnev.cryptoprice.domain.entity.Coin

object CoinDiffCallback : DiffUtil.ItemCallback<Coin>() {
    override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem.fromSymbol == newItem.fromSymbol
    }

    override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem == newItem
    }
}