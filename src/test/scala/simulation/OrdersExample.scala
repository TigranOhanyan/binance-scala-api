package simulation

import akka.actor.ActorSystem
import com.binance.api.client.{BinanceApiClientFactory, BinanceClientFactory, BinanceClientRESTSimulation}
import com.binance.api.client.domain._
import com.binance.api.client.domain.account.NewOrder.{limitBuy, marketBuy}
import com.binance.api.client.domain.account.request._
import com.binance.api.client.exception.BinanceApiExceptionError
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.util.{Failure, Success}
/**
 * Examples on how to place orders, cancel them, and query account information.
 */
object OrdersExample extends App {

  val config: Config = ConfigFactory.load()
  val binanceConfig: Config = config.getConfig("binance")
  implicit val actorSystem: ActorSystem = ActorSystem()
//  import
  val factory = BinanceClientFactory.fromConfig(binanceConfig)
  val client  = factory.newAsyncRestClient
  val symbol  = Symbol("SIMULATION")

  //  // Getting list of open orders
  //  client.getOpenOrders(OrderRequest(symbol)).foreach(println)
  //  // Getting list of all orders with a limit of 10
  //  client.getAllOrders(AllOrdersRequest(symbol, limit = Some(10))).foreach(println)
  //  // Get status of a particular order
  //  client.getOrderStatus(OrderStatusRequest(symbol, orderId = Some(OrderId(751698L)))).foreach(println)
  //
  //  // Canceling an order
  //  client.cancelOrder(CancelOrderRequest(symbol, orderId = Some(OrderId(756762L)))).failed.foreach {
  //    case e: BinanceApiExceptionError => println(e)
  //    case other => other.printStackTrace()
  //  }
  //
  //  // Placing a test LIMIT order
  //  client.newOrderTest(limitBuy(symbol.value, TimeInForce.GTC, BigDecimal("1000"), BigDecimal("0.0001"))).foreach(println)
  //  // Placing a test MARKET order
  //  client.newOrderTest(marketBuy(symbol.value, BigDecimal("1000"))).foreach(println)
  //   Placing a real LIMIT order
  client.getBookTickers
  client.newOrder(marketBuy(symbol.value,BigDecimal("0.01"))).onComplete{
    case Success(value) => actorSystem.log.info(value.toString)
    case Failure(BinanceClientRESTSimulation.TickerEnd) => endSimulation()
    case Failure(error: Throwable) => actorSystem.log.error("Invalid Ticker", error)
  }
  client match {
    case c: BinanceClientRESTSimulation =>
      c.close()
    case _ =>
  }

  endSimulation()

  def endSimulation(): Unit = {
    actorSystem.terminate()
    actorSystem.log.info("Simulation Terminated!")
  }
}
