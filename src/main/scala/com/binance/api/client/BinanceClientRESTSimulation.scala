package com.binance.api.client
import java.io._

import akka.Done
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.binance.api.client.BinanceClientRESTSimulation.TickerEnd
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderStdResponse
import com.binance.api.client.domain.account.{NewOrder, NewOrderResponse}
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.domain.market.{AllBookTicker, BookTicker}
import com.binance.api.client.domain.{OrderSide, OrderStatus}

import scala.concurrent.Future
import scala.util.Random

class BinanceClientRESTSimulation(priceFile: File, ordersFile: File) extends BinanceClientREST {

  ordersFile.createNewFile()
  val pr: BufferedReader = new BufferedReader(new FileReader(priceFile))
  val or: BufferedWriter = new BufferedWriter(new FileWriter(ordersFile))
  or.write(BinanceClientRESTSimulation.orderFileHeader)
  or.write("\n")

  var _tickers: Option[AllBookTicker] = None

  override def getBookTickers(implicit um: FromEntityUnmarshaller[AllBookTicker]): Future[AllBookTicker] = {
    Option(pr.readLine()) match {
      case Some(line: String) =>
        val tickers: AllBookTicker = BinanceClientRESTSimulation.parseTickers(line)
        _tickers = Some(tickers)
        Future.successful(tickers)
      case _ => Future.failed(TickerEnd)
    }
  }

  override def ping: Future[Done] =
    Future.failed(new RuntimeException("Simulation Mode does not support Ping request!"))


  override def getServerTime: Future[ServerTime] =
    Future.failed(new RuntimeException("Simulation Mode does not support Server Time request!"))


  override def getExchangeInfo: Future[ExchangeInfo] =
    Future.failed(new RuntimeException("Simulation Mode does not support Exchange Info request!"))

  /**
   * Send a new order
   *
   */
  override def newOrder(order: NewOrder.NewFullOrder): Future[NewOrderResponse.NewOrderStdResponse] = {
    val orderId: Long = Random.nextInt(Int.MaxValue).toLong
    val cOID: String = order.newClientOrderId.getOrElse(s"$orderId")
    _tickers.flatMap(_.tickers.find(_.symbol == order.symbol)) match {
      case Some(ticker: BookTicker) =>
        val executionPrice: BigDecimal = order.orderSide match {
          case OrderSide.BUY => ticker.askPrice
          case OrderSide.SELL => ticker.bidPrice
        }
        val response: NewOrderStdResponse = NewOrderStdResponse(
          order.symbol,
          orderId,
          cOID,
          System.currentTimeMillis(),
          executionPrice,
          order.quantity,
          order.quantity,
          cumulativeQuoteQty = order.quantity * executionPrice,
          OrderStatus.FILLED,
          order.orderType,
          order.orderSide,
          ""
        )
        or.write(BinanceClientRESTSimulation.serializeTrade(response))
        or.write("\n")
        Future.successful(response)
      case _ => Future.failed(TickerEnd)
    }

  }

  def close(): Unit = {
    pr.close()
    or.close()
  }
}

object BinanceClientRESTSimulation {

  val simulationSymbol: String = "SIMULATION"

  def parseTicker(line: String): BookTicker = line.split(",") match {
    case Array(_mid: String) =>
      val mid: BigDecimal = BigDecimal(_mid)
      BookTicker(simulationSymbol, mid, BigDecimal(0.0), mid, BigDecimal(0.0))
    case _ => throw InvalidTicker
  }

  def parseTickers(line: String): AllBookTicker = AllBookTicker(List(parseTicker(line)))

  def serializeTrade(orderResponse: NewOrderStdResponse): String = {
    Array(
      s"${orderResponse.orderId}",
      orderResponse.clientOrderId,
      s"${orderResponse.transactTime}",
      orderResponse.symbol,
      orderResponse.orderSide.name(),
      orderResponse.orderStatus.name(),
      orderResponse.orderType.name(),
      orderResponse.origQty.bigDecimal.toPlainString,
      orderResponse.executedQty.bigDecimal.toPlainString,
      orderResponse.price.bigDecimal.toPlainString,
      orderResponse.cumulativeQuoteQty.bigDecimal.toPlainString,
    ).mkString(",")
  }

  val orderFileHeader: String = Array(
    "orderId",
    "clientOrderId",
    "timestamp",
    "symbol",
    "side",
    "status",
    "type",
    "origQty",
    "executedQty",
    "price",
    "cumulativeQuoteQty",
  ).mkString(",")


  object TickerEnd extends RuntimeException("Ticker END")
  object InvalidTicker extends RuntimeException("Invalid ticker from csv line!")

}