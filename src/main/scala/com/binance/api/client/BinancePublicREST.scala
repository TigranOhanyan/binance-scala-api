package com.binance.api.client

import akka.Done
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.domain.market.AllBookTicker

import scala.concurrent.Future

trait BinancePublicREST {

  def getBookTickers(implicit um: FromEntityUnmarshaller[AllBookTicker]): Future[AllBookTicker]

  def ping: Future[Done]

  def getServerTime: Future[ServerTime]

  def getExchangeInfo: Future[ExchangeInfo]

}

