package com.grebnev.cryptoprice.database

import android.os.Build
import androidx.room.Room
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.database.CoinDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
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
        database =
            Room
                .inMemoryDatabaseBuilder(
                    RuntimeEnvironment.getApplication(),
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
    fun `insertCoinList and getCoinList should return all coins ordered by market cap`() =
        runTest {
            val testCoins = listOf(btcCoin, ethCoin)

            coinDao.insertCoinList(testCoins)
            val result = coinDao.getCoinList().first()

            Assert.assertEquals(2, result.size)
            Assert.assertEquals("BTC", result[0].fromSymbol)
            Assert.assertEquals("ETH", result[1].fromSymbol)
        }

    @Test
    fun `getCoinFromSymbol should return correct coin`() =
        runTest {
            val testCoin = btcCoin
            coinDao.insertCoinList(listOf(testCoin))

            val result = coinDao.getCoinFromSymbol("BTC").first()

            Assert.assertEquals(testCoin.fromSymbol, result.fromSymbol)
            Assert.assertEquals(testCoin.price, result.price)
            Assert.assertEquals(testCoin.lastMarket, result.lastMarket)
        }

    @Test
    fun `getTimeLastUpdate should return latest timestamp`() =
        runTest {
            val currentTime = System.currentTimeMillis()
            val oldBtc = btcCoin.copy(lastUpdate = currentTime - 10000)
            val currentEth = ethCoin.copy(lastUpdate = currentTime)
            coinDao.insertCoinList(listOf(oldBtc, currentEth))

            val result = coinDao.getTimeLastUpdate().first()

            Assert.assertEquals(currentTime, result)
        }

    @Test
    fun `insertCoinList with same fromSymbol should replace existing`() =
        runTest {
            val originalCoin = btcCoin
            coinDao.insertCoinList(listOf(originalCoin))

            val updatedCoin = originalCoin.copy(price = 55000.0, lastMarket = "Kraken")

            coinDao.insertCoinList(listOf(updatedCoin))
            val result = coinDao.getCoinFromSymbol("BTC").first()

            Assert.assertEquals(updatedCoin.price, result.price)
            Assert.assertEquals(updatedCoin.lastMarket, result.lastMarket)
            Assert.assertEquals(1, coinDao.getCoinList().first().size)
        }
}