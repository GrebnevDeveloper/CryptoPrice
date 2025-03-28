package com.grebnev.cryptoprice.data.mapper

import com.grebnev.core.extensions.convertTimestampToTimeByPattern
import com.grebnev.cryptoprice.data.network.model.news.NewsResponseDto
import com.grebnev.cryptoprice.domain.entity.News
import javax.inject.Inject

class NewsMapper
    @Inject
    constructor() {
        fun mapNewsResponseToNewsEntity(newsResponseDto: NewsResponseDto): List<News> {
            val newsDto = newsResponseDto.responseData
            val listNews = mutableListOf<News>()
            newsDto.forEach {
                val news =
                    News(
                        id = it.id,
                        publishedOn = it.publishedOn.convertTimestampToTimeByPattern("dd.MM.yyyy HH:mm"),
                        imageUrl = it.imageUrl,
                        sourceUrl = it.sourceUrl,
                        title = it.title,
                        body = it.body,
                    )
                listNews.add(news)
            }

            return listNews
        }
    }