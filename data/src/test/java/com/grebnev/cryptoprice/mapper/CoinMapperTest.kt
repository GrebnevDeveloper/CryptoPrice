package com.grebnev.cryptoprice.mapper

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.grebnev.core.extensions.convertTimestampToTimeByPattern
import com.grebnev.cryptoprice.data.database.CoinDbModel
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.model.coin.CoinDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinJsonContainerDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinNameContainerDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinNameDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinNameListDto
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class CoinMapperTest {
    private lateinit var mapper: CoinMapper
    private lateinit var gson: Gson
    private var currentTime: Long = 0

    @Before
    fun setUp() {
        mapper = CoinMapper()
        gson = Gson()
        currentTime = System.currentTimeMillis()
    }

    @Test
    fun `mapDtoToDbModel should convert CoinDto to CoinDbModel correctly`() {
        val coinDto =
            CoinDto(
                fromSymbol = "BTC",
                toSymbol = "USD",
                price = 50000.0,
                lastUpdate = currentTime,
                highDay = 51000.0,
                lowDay = 49000.0,
                lastMarket = "Binance",
                imageUrl = "/media/37746251/btc.png",
                mktCap = 1_000_000_000.0,
                changePct24Hour = 2.5,
                changePctDay = 1.2,
                openDay = 49500.0,
                volumeDayTo = 50_000_000.0,
            )

        val result = mapper.mapDtoToDbModel(coinDto)

        assertEquals("BTC", result.fromSymbol)
        assertEquals("USD", result.toSymbol)
        assertEquals(50000.0, result.price)
        assertEquals(currentTime, result.lastUpdate)
        assertEquals("https://cryptocompare.com/media/37746251/btc.png", result.imageUrl)
        assertEquals(1_000_000_000.0, result.mktCap)
        assertEquals(2.5, result.changePct24Hour)
        assertEquals(1.2, result.changePctDay)
        assertEquals(49500.0, result.openDay)
        assertEquals(50_000_000.0, result.volumeDay)
    }

    @Test
    fun `mapJsonContainerDtoToCoinDtoList should parse json correctly`() {
        val json =
            """
            {
                "BTC": {
                    "USD": {
                        "FROMSYMBOL": "BTC",
                        "TOSYMBOL": "USD",
                        "PRICE": 50000.0,
                        "LASTUPDATE": $currentTime,
                        "HIGHDAY": 51000.0,
                        "LOWDAY": 49000.0,
                        "LASTMARKET": "Binance",
                        "IMAGEURL": "/media/37746251/btc.png",
                        "MKTCAP": 1000000000.0,
                        "CHANGEPCT24HOUR": 2.5,
                        "CHANGEPCTDAY": 1.2,
                        "OPENDAY": 49500.0,
                        "VOLUMEDAYTO": 50000000.0
                    }
                }
            }
            """.trimIndent()

        val jsonContainer =
            CoinJsonContainerDto(
                json = gson.fromJson(json, JsonObject::class.java),
            )

        val result = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)

        assertEquals(1, result.size)
        assertEquals("BTC", result[0].fromSymbol)
        assertEquals("USD", result[0].toSymbol)
        assertEquals(50000.0, result[0].price)
        assertEquals(currentTime, result[0].lastUpdate)
        assertEquals("/media/37746251/btc.png", result[0].imageUrl)
    }

    @Test
    fun `mapJsonContainerDtoToCoinDtoList should return empty list for null json`() {
        val jsonContainer =
            CoinJsonContainerDto(
                json = null,
            )

        val result = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `mapNamesListToString should join names correctly`() {
        val nameListDto =
            CoinNameListDto(
                names =
                    listOf(
                        CoinNameContainerDto(
                            coinNameDto =
                                CoinNameDto(
                                    name = "BTC",
                                ),
                        ),
                        CoinNameContainerDto(
                            coinNameDto =
                                CoinNameDto(
                                    name = "ETH",
                                ),
                        ),
                    ),
            )

        val result = mapper.mapNamesListToString(nameListDto)

        assertEquals("BTC,ETH", result)
    }

    @Test
    fun `mapNamesListToString should return empty string for null names`() {
        val nameListDto = CoinNameListDto(names = null)

        val result = mapper.mapNamesListToString(nameListDto)

        assertEquals("", result)
    }

    @Test
    fun `mapDbModelToEntity should convert CoinDbModel to Coin correctly`() {
        val timestamp = 1672531200L
        val pattern = "HH:mm:ss"
        val timeZone = TimeZone.getTimeZone("UTC")
        val coinDbModel =
            CoinDbModel(
                fromSymbol = "BTC",
                toSymbol = "USD",
                price = 50000.0,
                lastUpdate = timestamp,
                highDay = 51000.0,
                lowDay = 49000.0,
                lastMarket = "Binance",
                imageUrl = "https://cryptocompare.com/media/37746251/btc.png",
                mktCap = 1_000_000_000.0,
                changePct24Hour = 2.5,
                changePctDay = 1.2,
                openDay = 49500.0,
                volumeDay = 50_000_000.0,
            )

        mockkStatic("com.grebnev.core.extensions.LongExKt")
        every {
            any<Long>().convertTimestampToTimeByPattern(
                pattern = eq(pattern),
                timeZone = timeZone,
            )
        } answers {
            val timestamp = firstArg<Long>()
            val pattern = secondArg<String>()
            val tz = thirdArg<TimeZone>()
            timestamp.convertTimestampToTimeByPattern(pattern, tz)
        }

        val result = mapper.mapDbModelToEntity(coinDbModel)

        assertEquals("BTC", result.fromSymbol)
        assertEquals("USD", result.toSymbol)
        assertEquals(50000.0, result.price)
        assertEquals("04:00:00", result.lastUpdate)
        assertEquals(51000.0, result.highDay)
        assertEquals(49000.0, result.lowDay)
        assertEquals("Binance", result.lastMarket)
        assertEquals("https://cryptocompare.com/media/37746251/btc.png", result.imageUrl)
        assertEquals(1_000_000_000.0, result.mktCap)
        assertEquals(2.5, result.changePct24Hour)
        assertEquals(1.2, result.changePctDay)
        assertEquals(49500.0, result.openDay)
        assertEquals(50_000_000.0, result.volumeDay)

        unmockkStatic("com.grebnev.core.extensions.LongExKt")
    }
}