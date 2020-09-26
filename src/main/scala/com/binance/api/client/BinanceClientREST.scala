package com.binance.api.client

import akka.Done
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.binance.api.client.domain.account.NewOrder.NewFullOrder
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderStdResponse
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.domain.market.AllBookTicker

import scala.concurrent.Future

trait BinanceClientREST extends BinancePublicREST {


  /**
   * Send a new order
   *
   */
  def newOrder(order: NewFullOrder): Future[NewOrderStdResponse]

}
