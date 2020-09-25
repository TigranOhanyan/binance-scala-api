package com.binance.api.client.impl

import java.lang.System.currentTimeMillis

import com.binance.api.client.BinanceApiAsyncRestClient
import com.binance.api.client.constant.BinanceApiConstants.DEFAULT_RECEIVING_WINDOW
import com.binance.api.client.domain._
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderStdResponse
import com.binance.api.client.domain.account._
import com.binance.api.client.domain.account.request.{AllOrdersRequest, CancelOrderRequest, OrderRequest, OrderStatusRequest}
import com.binance.api.client.domain.event.ListenKey
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.domain.market._
import com.binance.api.client.json.Decoders._

import scala.concurrent.{ExecutionContext, Future}

trait AsJava[From, To] {
  def convert(from: From): To
}

object AsJava {
  def apply[From, To](from: From)(implicit asJava: AsJava[From, To]): To =
    asJava.convert(from)

  def apply[From, To](fromOpt: Option[From])(implicit asJava: AsJava[From, To]): To =
    fromOpt match {
      case Some(from) => apply(from)
      case None       => null.asInstanceOf[To]
    }

  def or[From, To](fromOpt: Option[From], or: To)(implicit asJava: AsJava[From, To]): To =
    fromOpt match {
      case Some(from) => apply(from)
      case None       => or
    }

  implicit val x0: AsJava[String, String]          = t => t
  implicit val x1: AsJava[Long, java.lang.Long]    = t => t
  implicit val x2: AsJava[Int, java.lang.Integer]  = t => t
  implicit val x3: AsJava[Instant, java.lang.Long] = _.millis
  implicit val x4: AsJava[Asset, String]           = _.value
  implicit val x5: AsJava[Quantity, String]        = _.value.toString
  implicit val x6: AsJava[Price, String]           = _.value.toString
  implicit val x7: AsJava[OrderId, java.lang.Long] = _.value
  implicit val x8: AsJava[Symbol, String]          = _.value.toUpperCase
  implicit val x9: AsJava[Amount, String]          = _.value.toString()

  implicit def instanceOpt[E <: java.lang.Enum[E]]: AsJava[E, E] = e => e
}

/**
  * Implementation of Binance's REST API using Retrofit with asynchronous/non-blocking method calls.
  */
class BinanceApiAsyncRestClientImpl(service: BinanceApiService)(implicit ex: ExecutionContext)
    extends BinanceApiAsyncRestClient {

  override def ping: Future[Unit] =
    RunRequest[Unit](service.ping)

  override def getServerTime: Future[ServerTime] =
    RunRequest[ServerTime](service.getServerTime)

  override def getExchangeInfo: Future[ExchangeInfo] =
    RunRequest[ExchangeInfo](service.getExchangeInfo)

  override def getOrderBook(symbol: Symbol, limit: Int): Future[OrderBook] =
    RunRequest[OrderBook](service.getOrderBook(symbol.value, limit))

  def getAggTrades(symbol:    Symbol,
                   fromId:    Option[String] = None,
                   limit:     Option[Int] = None,
                   startTime: Option[Instant] = None,
                   endTime:   Option[Instant] = None): Future[List[AggTrade]] =
    RunRequest[List[AggTrade]](
      service
        .getAggTrades(
          AsJava(symbol),
          AsJava(fromId),
          AsJava(limit),
          AsJava(startTime),
          AsJava(endTime)
        )
    )

  def getTrades(symbol:    Symbol,
                limit:     Option[Int] = None): Future[List[Trade]] =
    RunRequest[List[Trade]](
      service
        .getTrades(
          AsJava(symbol),
          AsJava(limit)
        )
    )

  def getCandlestickBars(symbol:    Symbol,
                         interval:  CandlestickInterval,
                         limit:     Option[Int] = None,
                         startTime: Option[Instant] = None,
                         endTime:   Option[Instant] = None): Future[List[Candlestick]] =
    RunRequest[List[Candlestick]](
      service
        .getCandlestickBars(AsJava(symbol),
                            AsJava(interval.intervalId),
                            AsJava(limit),
                            AsJava(startTime),
                            AsJava(endTime))
    )

  override def get24HrPriceStatistics(symbol: Symbol): Future[TickerStatistics] =
    RunRequest[TickerStatistics](
      service.get24HrPriceStatistics(
        AsJava(symbol)
      )
    )

  override def getAllPrices: Future[List[TickerPrice]] =
    RunRequest[List[TickerPrice]](service.getLatestPrices)

  override def getBookTickers: Future[List[BookTicker]] =
    RunRequest[List[BookTicker]](service.getBookTickers)

  override def newOrder(order: NewOrder): Future[NewOrderStdResponse] = ???
//    RunRequest[NewOrderStdResponse](
//      service.newOrder(
//        AsJava(order.symbol),
//        AsJava(order.side),
//        AsJava(order.`type`),
//        AsJava(order.timeInForce),
//        AsJava(order.quantity),
//        AsJava(order.price),
//        AsJava(order.stopPrice),
//        AsJava(order.icebergQty),
//        AsJava(order.recvWindow),
//        AsJava(order.timestamp)
//      )
//    )

  override def newOrderTest(in: NewOrder): Future[Unit] = ???
//    RunRequest[Unit](
//      service.newOrderTest(
//        AsJava(in.symbol),
//        AsJava(in.side),
//        AsJava(in.`type`),
//        AsJava(in.timeInForce),
//        AsJava(in.quantity),
//        AsJava(in.price),
//        AsJava(in.stopPrice),
//        AsJava(in.icebergQty),
//        AsJava(in.recvWindow),
//        AsJava(in.timestamp)
//      )
//    )

  override def getOrderStatus(in: OrderStatusRequest): Future[Order] =
    RunRequest[Order](
      service.orderStatus(
        AsJava(in.symbol),
        AsJava(in.orderId),
        AsJava(in.origClientOrderId),
        AsJava.or(in.recvWindow, DEFAULT_RECEIVING_WINDOW),
        AsJava.or(in.timestamp, currentTimeMillis)
      )
    )

  override def cancelOrder(in: CancelOrderRequest): Future[Unit] =
    RunRequest[Unit](
      service.cancelOrder(
        AsJava(in.symbol),
        AsJava(in.orderId),
        AsJava(in.origClientOrderId),
        AsJava(in.newClientOrderId),
        AsJava.or(in.recvWindow, DEFAULT_RECEIVING_WINDOW),
        AsJava.or(in.timestamp, currentTimeMillis)
      )
    )

  override def getOpenOrders(in: OrderRequest): Future[List[Order]] =
    RunRequest[List[Order]](
      service.openOrders(AsJava(in.symbol),
                         AsJava.or(in.recvWindow, DEFAULT_RECEIVING_WINDOW),
                         AsJava.or(in.timestamp, currentTimeMillis))
    )

  override def getAllOrders(in: AllOrdersRequest): Future[List[Order]] =
    RunRequest[List[Order]](
      service.allOrders(
        AsJava(in.symbol),
        AsJava(in.orderId),
        AsJava(in.limit),
        AsJava.or(in.recvWindow, DEFAULT_RECEIVING_WINDOW),
        AsJava.or(in.timestamp, currentTimeMillis)
      )
    )

  def getAccount(recvWindow: Option[Long], timestamp: Option[Instant]): Future[Account] =
    RunRequest[Account](
      service.account(
        AsJava.or(recvWindow, DEFAULT_RECEIVING_WINDOW),
        AsJava.or(timestamp, currentTimeMillis)
      )
    )

  def getMyTrades(symbol:     Symbol,
                  limit:      Option[Int],
                  fromId:     Option[Long],
                  recvWindow: Option[Long],
                  timestamp:  Option[Instant]): Future[List[MyTrade]] =
    RunRequest[List[MyTrade]](
      service.getMyTrades(
        AsJava(symbol),
        AsJava(limit),
        AsJava(fromId),
        AsJava.or(recvWindow, DEFAULT_RECEIVING_WINDOW),
        AsJava.or(timestamp, currentTimeMillis)
      )
    )

  override def withdraw(asset: Asset, address: String, amount: Amount, name: Option[String] = None): Future[Unit] =
    RunRequest[Unit](
      service.withdraw(
        AsJava(asset),
        AsJava(address),
        AsJava(amount),
        AsJava(name),
        AsJava(DEFAULT_RECEIVING_WINDOW),
        AsJava(currentTimeMillis)
      )
    )

  override def getDepositHistory(asset: Asset): Future[DepositHistory] =
    RunRequest[DepositHistory](
      service
        .depositHistory(
          AsJava(asset),
          AsJava(DEFAULT_RECEIVING_WINDOW),
          AsJava(currentTimeMillis)
        )
    )

  override def getWithdrawHistory(asset: Asset): Future[WithdrawHistory] =
    RunRequest[WithdrawHistory](
      service
        .withdrawHistory(
          AsJava(asset.value),
          AsJava(DEFAULT_RECEIVING_WINDOW),
          AsJava(currentTimeMillis)
        )
    )

  override def getDepositAddress(asset: Asset): Future[DepositAddress] =
    RunRequest[DepositAddress](
      service
        .depositAddress(
          AsJava(asset),
          AsJava(DEFAULT_RECEIVING_WINDOW),
          AsJava(currentTimeMillis)
        )
    )

  override def startUserDataStream: Future[ListenKey] =
    RunRequest[ListenKey](service.startUserDataStream)

  override def keepAliveUserDataStream(listenKey: ListenKey): Future[Unit] =
    RunRequest[Unit](service.keepAliveUserDataStream(listenKey.listenKey))

  override def closeUserDataStream(listenKey: ListenKey): Future[Unit] =
    RunRequest[Unit](service.closeAliveUserDataStream(listenKey.listenKey))
}
