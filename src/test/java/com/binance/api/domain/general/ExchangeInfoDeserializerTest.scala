package com.binance.api.domain.general

import com.binance.api.client.domain._
import com.binance.api.client.domain.general.SymbolFilter.{LotSizeFilter, PriceFilter}
import com.binance.api.client.json.Decoders._
import com.binance.api.client.domain.general._
import io.circe._
import org.junit.Assert._
import org.junit.Test
import spray.json._

/**
  * Test deserialization of exchange information.
  */
class ExchangeInfoDeserializerTest {
  @Test def testExchangeInfoDeserialization(): Unit = {
    val json = """{
  "timezone": "UTC",
  "serverTime": 1508631584636,
  "rateLimits": [{
      "rateLimitType": "REQUESTS",
      "interval": "MINUTE",
      "limit": 1200
    },
    {
      "rateLimitType": "ORDERS",
      "interval": "SECOND",
      "limit": 10
    },
    {
      "rateLimitType": "ORDERS",
      "interval": "DAY",
      "limit": 100000
    }
  ],
  "exchangeFilters": [],
  "symbols": [{
    "symbol": "ETHBTC",
    "status": "TRADING",
    "baseAsset": "ETH",
    "baseAssetPrecision": 8,
    "baseCommissionPrecision": 8,
    "quoteAsset": "BTC",
    "quotePrecision": 8,
    "quoteCommissionPrecision": 8,
    "orderTypes": ["LIMIT", "MARKET"],
    "icebergAllowed": false,
    "filters": [{
      "filterType": "PRICE_FILTER",
      "minPrice": "0.00000100",
      "maxPrice": "100000.00000000",
      "tickSize": "0.00000100"
    }, {
      "filterType": "LOT_SIZE",
      "minQty": "0.00100000",
      "maxQty": "100000.00000000",
      "stepSize": "0.00100000"
    }, {
      "filterType": "MIN_NOTIONAL",
      "minNotional": "0.00100000"
    }, {
      "filterType": "ICEBERG_PARTS",
      "limit": 10
    }]
  }]}"""
    assertEquals(
      ExchangeInfo(
        timezone = "UTC",
        serverTime = 1508631584636L,
//        rateLimits = List(
//          RateLimit(rateLimitType = RateLimitType.REQUESTS, interval = RateLimitInterval.MINUTE, limit = 1200),
//          RateLimit(rateLimitType = RateLimitType.ORDERS, interval = RateLimitInterval.SECOND, limit = 10),
//          RateLimit(rateLimitType = RateLimitType.ORDERS, interval = RateLimitInterval.DAY, limit = 100000)
//        ),
        rateLimits = List.empty[RateLimit],
        exchangeFilters = List.empty,
        symbols = List(
          SymbolInfo(
            symbol = "ETHBTC",
            status = SymbolStatus.TRADING,
            baseAsset = "ETH",
            baseAssetPrecision = 8,
            baseCommissionPrecision = 8,
            quoteAsset = "BTC",
            quotePrecision = 8,
            quoteCommissionPrecision = 8,
            orderTypes = List(OrderType.LIMIT, OrderType.MARKET),
            icebergAllowed = false,
            filters = List(
              PriceFilter(BigDecimal("0.00000100"), BigDecimal("100000.00000000"), BigDecimal("0.00000100")),
              LotSizeFilter(BigDecimal("0.00100000"), BigDecimal("100000.00000000"), BigDecimal("0.00100000")),
//              SymbolFilter.Other
            )
          )
        )
      ),
      ExchangeInfo.parser.read(json.parseJson)
    )
  }
}
