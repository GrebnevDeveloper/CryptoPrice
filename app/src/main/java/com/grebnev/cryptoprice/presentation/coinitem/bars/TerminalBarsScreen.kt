package com.grebnev.cryptoprice.presentation.coinitem.bars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grebnev.cryptoprice.R
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.presentation.coinitem.CoinItemViewModel
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun TerminalScreen(
    modifier: Modifier = Modifier,
    fromSymbol: String,
    viewModel: CoinItemViewModel,
) {
    // Получаем текущее состояние из viewModel
    val terminalBarsState = viewModel.barState.collectAsState()
    val currentTerminalBarsState = terminalBarsState.value
    // В зависимости от текущего состояния экрана отображаем соответствующий UI
    when (currentTerminalBarsState) {
        is TerminalBarsState.Content -> {
            // Если состояние - Content, отображаем основной контент экрана
            TerminalScreenContent(
                bars = currentTerminalBarsState.bars, // Список баров (данные графика)
                timeFrame = currentTerminalBarsState.timeFrame, // Выбранный временной интервал
                onTimeFrameSelected = {
                    viewModel.getBarsForCoin(
                        timeFrame = it,
                        fromSymbol = fromSymbol,
                    )
                }, // Обработчик выбора временного интервала
                modifier = modifier,
            )
        }

        is TerminalBarsState.Loading -> {
            // Если состояние - Loading, отображаем индикатор загрузки
            Box(
                modifier =
                    modifier
                        .fillMaxSize()
                        .background(Color.Black),
                // Черный фон
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator() // Индикатор загрузки
            }
        }

        is TerminalBarsState.Initial -> {
            // Если состояние - Initial, ничего не отображаем (можно добавить логику инициализации)
        }

        is TerminalBarsState.Error -> TODO()
    }
}

@Composable
private fun TerminalScreenContent(
    bars: List<Bar>, // Список баров (данные графика)
    timeFrame: TimeFrame, // Выбранный временной интервал
    onTimeFrameSelected: (TimeFrame) -> Unit, // Обработчик выбора временного интервала
    modifier: Modifier = Modifier,
) {
    // Состояние терминала, которое зависит от списка баров
    val terminalState = rememberTerminalState(bars = bars)

    // Отображаем график
    Chart(
        modifier = modifier,
        terminalState = terminalState,
        onTerminalStateChanged = {
            terminalState.value = it // Обновляем состояние терминала
        },
        timeFrame = timeFrame,
    )

    // Если есть хотя бы один бар, отображаем цены
    bars.firstOrNull()?.let {
        Prices(
            modifier = modifier,
            terminalState = terminalState,
            lastPrice = it.close, // Последняя цена закрытия
        )
    }

    // Отображаем выбор временного интервала
    TimeFrames(
        selectedFrame = timeFrame,
        onTimeFrameSelected = onTimeFrameSelected,
    )
}

@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame, // Выбранный временной интервал
    onTimeFrameSelected: (TimeFrame) -> Unit, // Обработчик выбора временного интервала
) {
    Box(
        modifier = Modifier.fillMaxSize(), // Занимаем весь доступный размер
        contentAlignment = Alignment.TopStart, // Выравниваем содержимое в верхний левый угол
    ) {
        // Отображаем строку с кнопками выбора временного интервала
        Row(
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Для каждого временного интервала создаем кнопку
            TimeFrame.entries.forEach { timeFrame ->
                val labelResId =
                    when (timeFrame) {
                        TimeFrame.MINUTE -> R.string.minute
                        TimeFrame.HOURLY -> R.string.hourly
                        TimeFrame.DAILY -> R.string.daily
                    }
                val isSelected = timeFrame == selectedFrame // Проверяем, выбран ли текущий интервал
                AssistChip(
                    onClick = { onTimeFrameSelected(timeFrame) }, // Обработчик нажатия
                    label = { Text(stringResource(labelResId)) }, // Текст кнопки
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) Color.White else Color.Black, // Цвет фона
                            labelColor = if (isSelected) Color.Black else Color.White, // Цвет текста
                        ),
                )
            }
        }
    }
}

@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>, // Состояние терминала
    onTerminalStateChanged: (TerminalState) -> Unit, // Обработчик изменения состояния терминала
    timeFrame: TimeFrame, // Выбранный временной интервал
) {
    val currentTerminalState = terminalState.value

    // Состояние для масштабирования и прокрутки графика
    val transformable =
        rememberTransformableState { zoomChange, panChange, _ ->
            // Вычисляем количество видимых баров после масштабирования
            val visibleBarsCount =
                (currentTerminalState.visibleBarsCount / zoomChange)
                    .roundToInt()
                    .coerceIn(MIN_VISIBLE_BARS_COUNT, currentTerminalState.bars.size)

            // Вычисляем смещение после прокрутки
            val scrolledBy =
                (currentTerminalState.scrolledBy + panChange.x)
                    .coerceIn(
                        0f,
                        currentTerminalState.bars.size * currentTerminalState.barWidth -
                            currentTerminalState.terminalWidth,
                    )

            // Обновляем состояние терминала
            onTerminalStateChanged(
                currentTerminalState.copy(
                    visibleBarsCount = visibleBarsCount,
                    scrolledBy = scrolledBy,
                ),
            )
        }

    // Измеритель текста для отображения меток
    val textMeasurer = rememberTextMeasurer()

    // Холст для отрисовки графика
    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black) // Черный фон
                .clipToBounds()
                .padding(
                    top = 32.dp,
                    bottom = 32.dp,
                    end = 40.dp,
                ).transformable(transformable) // Добавляем возможность масштабирования и прокрутки
                .onSizeChanged {
                    // При изменении размера холста обновляем состояние терминала
                    onTerminalStateChanged(
                        currentTerminalState.copy(
                            terminalWidth = it.width.toFloat(),
                            terminalHeight = it.height.toFloat(),
                        ),
                    )
                },
    ) {
        val min = currentTerminalState.min
        val pxPerPoint = currentTerminalState.pxPerPoint
        translate(left = currentTerminalState.scrolledBy) {
            // Отрисовываем каждый бар
            currentTerminalState.bars.forEachIndexed { index, bar ->
                val offsetX = size.width - index * currentTerminalState.barWidth
                drawTimeDelimiter(
                    bar = bar,
                    nextBar =
                        if (index < currentTerminalState.bars.size - 1) {
                            currentTerminalState.bars[index + 1]
                        } else {
                            null
                        },
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    textMeasurer = textMeasurer,
                )
                // Отрисовываем линию от минимума до максимума
                drawLine(
                    color = Color.White,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(offsetX, (size.height - (bar.low - min) * pxPerPoint).toFloat()),
                    end = Offset(offsetX, (size.height - (bar.high - min) * pxPerPoint).toFloat()),
                )
                // Отрисовываем линию от открытия до закрытия
                drawLine(
                    color = if (bar.open > bar.close) Color.Red else Color.Green,
                    strokeWidth = currentTerminalState.barWidth / 2,
                    start = Offset(offsetX, (size.height - (bar.open - min) * pxPerPoint).toFloat()),
                    end = Offset(offsetX, (size.height - (bar.close - min) * pxPerPoint).toFloat()),
                )
            }
        }
    }
}

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>, // Состояние терминала
    lastPrice: Double, // Последняя цена закрытия
) {
    val textMeasurer = rememberTextMeasurer()

    val currentTerminalState = terminalState.value

    val max = currentTerminalState.max
    val min = currentTerminalState.min
    val pxPerPoint = currentTerminalState.pxPerPoint

    // Холст для отрисовки цен
    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .padding(vertical = 32.dp),
    ) {
        drawPrices(textMeasurer, max.toFloat(), min.toFloat(), pxPerPoint.toFloat(), lastPrice.toFloat())
    }
}

private fun DrawScope.drawTimeDelimiter(
    bar: Bar, // Текущий бар
    nextBar: Bar?, // Следующий бар
    timeFrame: TimeFrame, // Выбранный временной интервал
    offsetX: Float, // Смещение по оси X
    textMeasurer: TextMeasurer, // Измеритель текста
) {
    val calendar = bar.calendar

    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Определяем, нужно ли рисовать разделитель времени
    val shouldDrawDelimiter =
        when (timeFrame) {
            TimeFrame.MINUTE -> {
                minutes == 0
            }

            TimeFrame.HOURLY, TimeFrame.DAILY -> {
                val nextBarDay = nextBar?.calendar?.get(Calendar.DAY_OF_MONTH)
                day != nextBarDay
            }
        }
    if (!shouldDrawDelimiter) return

    // Рисуем пунктирную линию разделителя
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        strokeWidth = 1.dp.toPx(),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        pathEffect =
            PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
            ),
    )

    // Формируем текст для отображения времени
    val nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val text =
        when (timeFrame) {
            TimeFrame.MINUTE -> {
                String.format(Locale.getDefault(), "%02d:00", hours)
            }

            TimeFrame.HOURLY, TimeFrame.DAILY -> {
                String.format("%s %s", day, nameOfMonth)
            }
        }
    val textLayoutResult =
        textMeasurer.measure(
            text = text,
            style =
                TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                ),
        )

    // Рисуем текст времени
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft =
            Offset(
                x = offsetX - textLayoutResult.size.width / 2,
                y = size.height,
            ),
    )
}

private fun DrawScope.drawPrices(
    textMeasurer: TextMeasurer, // Измеритель текста
    max: Float, // Максимальная цена
    min: Float, // Минимальная цена
    pxPerPoint: Float, // Пикселей на пункт
    lastPrice: Float, // Последняя цена закрытия
) {
    // Рисуем линию и текст для максимальной цены
    val maxPriceOffsetY = 0f
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY),
    )
    drawTextPrices(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY,
    )

    // Рисуем линию и текст для последней цены
    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY),
    )
    drawTextPrices(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY,
    )

    // Рисуем линию и текст для минимальной цены
    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY),
    )
    drawTextPrices(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minPriceOffsetY,
    )
}

private fun DrawScope.drawTextPrices(
    textMeasurer: TextMeasurer, // Измеритель текста
    price: Float, // Цена
    offsetY: Float, // Смещение по оси Y
) {
    val textLayoutResult =
        textMeasurer.measure(
            text = price.toString(),
            style =
                TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                ),
        )

    // Рисуем текст цены
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft =
            Offset(
                x = size.width - textLayoutResult.size.width - 4.dp.toPx(),
                y = offsetY,
            ),
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White, // Цвет линии
    start: Offset, // Начальная точка
    end: Offset, // Конечная точка
    strokeWidth: Float = 1.dp.toPx(), // Толщина линии
) {
    // Рисуем пунктирную линию
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect =
            PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
            ),
    )
}