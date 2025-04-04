package com.grebnev.cryptoprice

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.database.CoinDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoinDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var coinDao: CoinDao
    private lateinit var btcCoin: CoinDbModel
    private lateinit var ethCoin: CoinDbModel

    @Before
    fun setup() {
        btcCoin =
            CoinDbModel(
                fromSymbol = "BTC",
                toSymbol = "USD",
                price = 50000.0,
                lastUpdate = System.currentTimeMillis(),
                highDay = 51000.0,
                lowDay = 49000.0,
                lastMarket = "Binance",
                imageUrl = "https://example.com/btc.png",
                mktCap = 1000000000.0,
                changePct24Hour = 2.5,
                changePctDay = 1.2,
                openDay = 49500.0,
                volumeDay = 50000000.0,
            )

        ethCoin =
            CoinDbModel(
                fromSymbol = "ETH",
                toSymbol = "USD",
                price = 3000.0,
                lastUpdate = System.currentTimeMillis(),
                highDay = 3100.0,
                lowDay = 2900.0,
                lastMarket = "Coinbase",
                imageUrl = "https://example.com/eth.png",
                mktCap = 500000000.0,
                changePct24Hour = 1.5,
                changePctDay = 0.8,
                openDay = 2950.0,
                volumeDay = 30000000.0,
            )

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database =
            Room
                .inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java,
                ).allowMainThreadQueries()
                .build()

        coinDao = database.coinDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCoinList_and_getCoinList_shouldReturnAllCoinsOrderedByMktCap() =
        runTest {
            val testCoins = listOf(btcCoin, ethCoin)

            coinDao.insertCoinList(testCoins)
            val result = coinDao.getCoinList().first()

            assertEquals(2, result.size)
            assertEquals("BTC", result[0].fromSymbol)
            assertEquals("ETH", result[1].fromSymbol)
        }

    @Test
    fun getCoinFromSymbol_shouldReturnCorrectCoin() =
        runTest {
            val testCoin = btcCoin
            coinDao.insertCoinList(listOf(testCoin))

            val result = coinDao.getCoinFromSymbol("BTC").first()

            assertEquals(testCoin.fromSymbol, result.fromSymbol)
            assertEquals(testCoin.price, result.price)
            assertEquals(testCoin.lastMarket, result.lastMarket)
        }

    @Test
    fun getTimeLastUpdate_shouldReturnLatestTimestamp() =
        runTest {
            val currentTime = System.currentTimeMillis()
            val oldBtc = btcCoin.copy(lastUpdate = currentTime - 10000)
            val currentEth = ethCoin.copy(lastUpdate = currentTime)
            coinDao.insertCoinList(listOf(oldBtc, currentEth))

            val result = coinDao.getTimeLastUpdate().first()

            assertEquals(currentTime, result)
        }

    @Test
    fun insertCoinList_withSameFromSymbol_shouldReplaceExisting() =
        runTest {
            val originalCoin = btcCoin
            coinDao.insertCoinList(listOf(originalCoin))

            val updatedCoin = originalCoin.copy(price = 55000.0, lastMarket = "Kraken")

            coinDao.insertCoinList(listOf(updatedCoin))
            val result = coinDao.getCoinFromSymbol("BTC").first()

            assertEquals(updatedCoin.price, result.price)
            assertEquals(updatedCoin.lastMarket, result.lastMarket)
            assertEquals(1, coinDao.getCoinList().first().size)
        }
}