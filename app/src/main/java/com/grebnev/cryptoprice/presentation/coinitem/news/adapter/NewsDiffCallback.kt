package com.grebnev.cryptoprice.presentation.coinitem.news.adapter

import androidx.recyclerview.widget.DiffUtil
import com.grebnev.cryptoprice.domain.entity.News

object NewsDiffCallback : DiffUtil.ItemCallback<News>() {
    override fun areItemsTheSame(
        oldItem: News,
        newItem: News,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: News,
        newItem: News,
    ): Boolean = oldItem == newItem
}