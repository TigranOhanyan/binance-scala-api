package com.binance.api.examples

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.domain.general.FilterType
import com.binance.api.client.domain.Symbol
import com.binance.api.client.domain.general.SymbolFilter.PriceFilter

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Examples on how to use the general endpoints.
  */
object GeneralEndpointsExample extends App {

  val factory = new BinanceApiClientFactory("", "")
  val client  = factory.newAsyncRestClient

  // Test connectivity
  client.ping.foreach(println)
  // Check server time
  client.getServerTime.foreach(println)

  // Exchange info
  client.getExchangeInfo.foreach { exchangeInfo =>
    println(exchangeInfo.timezone)
    println(exchangeInfo.symbols)
    // Obtain symbol information
    val symbolInfo = exchangeInfo.getSymbolInfo("ETHBTC")
    println(symbolInfo.status)
    val priceFilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER)
    println(priceFilter.asInstanceOf[PriceFilter].minPrice)
    println(priceFilter.asInstanceOf[PriceFilter].tickSize)
  }
}
