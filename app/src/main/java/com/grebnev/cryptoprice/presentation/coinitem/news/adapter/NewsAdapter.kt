package com.grebnev.cryptoprice.presentation.coinitem.news.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.grebnev.cryptoprice.databinding.ItemCoinNewsBinding
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.presentation.base.WebViewActivity
import com.squareup.picasso.Picasso

class NewsAdapter : ListAdapter<News, NewsViewHolder>(NewsDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewsViewHolder {
        val binding =
            ItemCoinNewsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int,
    ) {
        val news = getItem(position)
        with(holder.binding) {
            tvTitle.text = news.title
            Picasso.get().load(news.imageUrl).into(ivNewsImage)
            tvPublishedOn.text = news.publishedOn
            tvTitle.setOnClickListener {
                val context = holder.itemView.context
                val intent =
                    Intent(context, WebViewActivity::class.java).apply {
                        putExtra(WebViewActivity.URL_KEY, news.sourceUrl)
                    }
                context.startActivity(intent)
            }
        }
    }
}