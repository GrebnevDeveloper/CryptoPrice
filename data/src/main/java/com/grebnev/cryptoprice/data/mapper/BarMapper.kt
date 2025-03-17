package com.grebnev.cryptoprice.data.mapper

import com.grebnev.cryptoprice.data.network.model.bars.BarContainerDto
import com.grebnev.cryptoprice.domain.entity.Bar
import javax.inject.Inject

class BarMapper
    @Inject
    constructor() {
        fun mapBarContainerDtoToBarEntity(barContainerDto: BarContainerDto): List<Bar> {
            val barResponseDto = barContainerDto.containerData
            val barsDto = barResponseDto.responseData
            val bars = mutableListOf<Bar>()
            barsDto.forEach {
                val bar =
                    Bar(
                        high = it.high,
                        low = it.low,
                        open = it.open,
                        close = it.close,
                        time = it.time,
                    )
                bars.add(bar)
            }
            return bars
        }
    }