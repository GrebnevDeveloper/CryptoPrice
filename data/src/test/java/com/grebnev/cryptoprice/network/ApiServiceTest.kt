package com.grebnev.cryptoprice.network

import android.os.Build
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.data.network.AuthInterceptor
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class ApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient
                        .Builder()
                        .addInterceptor(AuthInterceptor("test_api_key"))
                        .addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            },
                        ).build(),
                ).build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTopCoinsInfo should returns coin list when request is successful`() =
        runTest {
            val mockResponse =
                """
                {
                    "Data": [
                        {
                            "CoinInfo": {
                                "Name": "BTC",
                                "FullName": "Bitcoin"
                            },
                            "RAW": {
                                "USD": {
                                    "PRICE": 50000.0
                                }
                            }
                        }
                    ]
                }
                """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200),
            )

            val response = apiService.getTopCoinsInfo(limit = 5)

            Assert.assertNotNull(response)
            TestCase.assertEquals(1, response.names?.size)
            TestCase.assertEquals(
                "BTC",
                response.names
                    ?.get(0)
                    ?.coinNameDto
                    ?.name,
            )

            val request = mockWebServer.takeRequest()
            TestCase.assertEquals("GET", request.method)
            TestCase.assertEquals("/top/totalvolfull?limit=5&tsym=USD", request.path)
            TestCase.assertEquals("Apikey test_api_key", request.headers["authorization"])
        }

    @Test
    fun `getFullPriceList should returns price data for multiple coins`() =
        runTest {
            val mockResponse =
                """
                {
                    "RAW": {
                        "BTC": {
                            "USD": {
                                "PRICE": 50000.0,
                                "LASTUPDATE": 1234567890
                            }
                        },
                        "ETH": {
                            "USD": {
                                "PRICE": 3000.0,
                                "LASTUPDATE": 1234567890
                            }
                        }
                    }
                }
                """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200),
            )

            val response = apiService.getFullPriceList(fSyms = "BTC,ETH")

            Assert.assertNotNull(response)
            TestCase.assertEquals(2, response.json?.size())

            val request = mockWebServer.takeRequest()
            TestCase.assertEquals("GET", request.method)
            TestCase.assertEquals("/pricemultifull?fsyms=BTC%2CETH&tsyms=USD", request.path)
            TestCase.assertEquals("Apikey test_api_key", request.headers["authorization"])
        }

    @Test
    fun `getBarsForCoin should returns historical data`() =
        runTest {
            val mockResponse =
                """
                {
                    "Data": {
                        "Data": [
                            {
                                "time": 1234567890,
                                "open": 50000.0,
                                "high": 51000.0,
                                "low": 49000.0,
                                "close": 50500.0
                            }
                        ]
                    }
                }
                """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200),
            )

            val response =
                apiService.getBarsForCoin(
                    timeFrame = "histoday",
                    fSyms = "BTC",
                    limit = 30,
                )

            Assert.assertNotNull(response)
            TestCase.assertEquals(1, response.containerData.responseData.size)

            val request = mockWebServer.takeRequest()
            TestCase.assertEquals("GET", request.method)
            TestCase.assertEquals("/v2/histoday?fsym=BTC&limit=30&tsym=USD", request.path)
            TestCase.assertEquals("Apikey test_api_key", request.headers["authorization"])
        }

    @Test
    fun `getNewsForCoin should returns news items`() =
        runTest {
            val mockResponse =
                """
                {
                    "Data": [
                        {
                            "title": "Bitcoin News",
                            "body": "Latest Bitcoin developments",
                            "published_on": 1234567890
                        }
                    ]
                }
                """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200),
            )

            val response = apiService.getNewsForCoin(category = "BTC")

            Assert.assertNotNull(response)
            TestCase.assertEquals(1, response.responseData.size)
            TestCase.assertEquals("Bitcoin News", response.responseData[0].title)

            val request = mockWebServer.takeRequest()
            TestCase.assertEquals("GET", request.method)
            TestCase.assertEquals("/v2/news/?lang=EN&categories=BTC", request.path)
            TestCase.assertEquals("Apikey test_api_key", request.headers["authorization"])
        }

    @Test(expected = HttpException::class)
    fun `getTopCoinsInfo should throws exception on server error`() =
        runTest {
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
            apiService.getTopCoinsInfo()
        }
}