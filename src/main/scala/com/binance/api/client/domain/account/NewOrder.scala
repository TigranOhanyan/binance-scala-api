package com.binance.api.client.domain.account

import akka.http.scaladsl.model.Uri
import com.binance.api.client.domain._

/**
  * A trade order to enter or exit a position.
  */
object NewOrder {

  /**
    * Places a MARKET buy order for the given <code>quantity</code>.
    *
    * @return a new order which is pre-configured with MARKET as the order type and BUY as the order side.
    */
  def marketBuy(symbol: String, quantity: BigDecimal) =
    NewFullOrder(symbol, OrderSide.BUY, OrderType.MARKET, None, quantity)

  /**
    * Places a MARKET sell order for the given <code>quantity</code>.
    *
    * @return a new order which is pre-configured with MARKET as the order type and SELL as the order side.
    */
  def marketSell(symbol: String, quantity: BigDecimal) =
    NewFullOrder(symbol, OrderSide.SELL, OrderType.MARKET, None, quantity)

  /**
    * Places a LIMIT buy order for the given <code>quantity</code> and <code>price</code>.
    *
    * @return a new order which is pre-configured with LIMIT as the order type and BUY as the order side.
    */
  def limitBuy(symbol: String, timeInForce: TimeInForce, quantity: BigDecimal, price: BigDecimal) =
    NewFullOrder(symbol, OrderSide.BUY, OrderType.LIMIT, Some(timeInForce), quantity, Some(price))

  /**
    * Places a LIMIT sell order for the given <code>quantity</code> and <code>price</code>.
    *
    * @return a new order which is pre-configured with LIMIT as the order type and SELL as the order side.
    */
  def limitSell(symbol: String, timeInForce: TimeInForce, quantity: BigDecimal, price: BigDecimal) =
    NewFullOrder(symbol, OrderSide.SELL, OrderType.LIMIT, Some(timeInForce), quantity, Some(price))


  /**
   *
   * @param symbol Symbol to place the order on.
   * @param orderSide Buy/Sell order side.
   * @param orderType Type of order.
   * @param timeInForce Time in force to indicate how long will the order remain active.
   * @param quantity Quantity.
   * @param price Price.
   * @param newClientOrderId A unique id for the order. Automatically generated if not sent.
   * @param stopPrice Used with stop orders..
   * @param icebergQty Used with iceberg orders.
   * @param recvWindow Receiving window.
   * @param timestamp Order timestamp.
   */
  case class NewAckOrder(
                          symbol: String,
                          orderSide: OrderSide,
                          orderType: OrderType,
                          timeInForce: Option[TimeInForce],
                          quantity: BigDecimal,
                          override val price: Option[BigDecimal] = None,
                          override val newClientOrderId: Option[String] = None,
                          override val stopPrice: Option[BigDecimal] = None,
                          override val icebergQty: Option[BigDecimal] = None,
                          override val recvWindow: Option[Long] = None,
                          override val timestamp: Option[Long] = None
                        ) extends NewOrder {
    override val newOrderRespType: NewOrderRespType = NewOrderRespType.ACK

    override val query: Uri.Query = super.query

  }

  /**
   *
   * @param symbol Symbol to place the order on.
   * @param orderSide Buy/Sell order side.
   * @param orderType Type of order.
   * @param timeInForce Time in force to indicate how long will the order remain active.
   * @param quantity Quantity.
   * @param price Price.
   * @param newClientOrderId A unique id for the order. Automatically generated if not sent.
   * @param stopPrice Used with stop orders..
   * @param icebergQty Used with iceberg orders.
   * @param recvWindow Receiving window.
   * @param timestamp Order timestamp.
   */
  case class NewFullOrder(
                           symbol: String,
                           orderSide: OrderSide,
                           orderType: OrderType,
                           timeInForce: Option[TimeInForce],
                           quantity: BigDecimal,
                           override val price: Option[BigDecimal] = None,
                           override val newClientOrderId: Option[String] = None,
                           override val stopPrice: Option[BigDecimal] = None,
                           override val icebergQty: Option[BigDecimal] = None,
                           override val recvWindow: Option[Long] = None,
                           override val timestamp: Option[Long] = None,
                         ) extends NewOrder {
    override val newOrderRespType: NewOrderRespType = NewOrderRespType.FULL

    override val query: Uri.Query = super.query
  }
}

sealed trait NewOrder extends QuerySerializable {
  /**
   * Symbol to place the order on.
   */
  def symbol: String
  /**
   * Buy/Sell order side.
   */
  def orderSide: OrderSide
  /**
   * Type of order.
   */
  def orderType: OrderType
  /**
   * Time in force to indicate how long will the order remain active.
   */
  def timeInForce: Option[TimeInForce]
  /**
   * Quantity.
   */
  def quantity: BigDecimal
  /**
   * Price.
   */
  def price: Option[BigDecimal] = None
  /**
   * A unique id for the order. Automatically generated if not sent.
   */
  def newClientOrderId: Option[String] = None
  /**
   * Used with stop orders.
   */
  def stopPrice: Option[BigDecimal] = None
  /**
   * Used with iceberg orders.
   */
  def icebergQty: Option[BigDecimal] = None
  /**
   * Receiving window.
   */
  def recvWindow: Option[Long] = None
  /**
   * Order timestamp.
   */
  def timestamp: Option[Long] = None

  /**
   * Response Type.
   */
  def newOrderRespType: NewOrderRespType


  /**
   * query.
   */
  def query: Uri.Query = {
    var query: Uri.Query = Uri.Query()
    query = query.+:("symbol" -> symbol)
    query = query.+:("side" -> orderSide.name())
    query = query.+:("type" -> orderType.name())
    timeInForce.foreach{ _timeInForce =>
      query = query.+:("timeInForce" -> _timeInForce.name())
    }
    query = query.+:("quantity" -> quantity.bigDecimal.toPlainString)
    price.foreach{ price =>
      query = query.+:("price" -> price.bigDecimal.toPlainString)
    }
    stopPrice.foreach{ stopPrice =>
      query = query.+:("stopPrice" -> stopPrice.bigDecimal.toPlainString)
    }
    newClientOrderId.foreach{ cOID =>
      query = query.+:("newClientOrderId" -> cOID)
    }
    icebergQty.foreach{ icebergQty =>
      query = query.+:("icebergQty" -> icebergQty.bigDecimal.toPlainString)
    }
    recvWindow.foreach{ recvWindow =>
      query = query.+:("recvWindow" -> recvWindow.toString)
    }
    query = query.+:("newOrderRespType" -> newOrderRespType.name())
    query = query.+:("timestamp" -> timestamp.getOrElse(System.currentTimeMillis()).toString)
    query
  }
}



trait QuerySerializable{

  def query: Uri.Query

}