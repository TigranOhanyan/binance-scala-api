package com.binance.api.client.domain.account

import com.binance.api.client.domain.{Asset, Instant, OrderId, OrderSide, OrderStatus, OrderType}
import spray.json.{DeserializationException, JsNumber, JsString, JsValue, RootJsonReader}

/**
  * Response returned when placing a new order on the system.
  *
  * @see NewOrder for the request
  */

sealed trait NewOrderResponse extends Serializable with Product {

  /**
   * Order symbol.
   */
  def symbol: String

  /**
   * Order id.
   */
  def orderId: Long
  /**
   * This will be either a generated one, or the newClientOrderId parameter
   * which was passed when creating the new order.
   */

  def clientOrderId: String

  /**
   * Transact time for this order.
   */
  def transactTime: Long
}

object NewOrderResponse {

  /**
   *
   * @param symbol Order symbol.
   * @param orderId       Order id.
   * @param clientOrderId This will be either a generated one, or the newClientOrderId parameter
   *                      which was passed when creating the new order.
   * @param transactTime        Transact time for this order.
   */
  case class NewOrderAckResponse(symbol: String, orderId: Long, clientOrderId: String, transactTime: Long) extends NewOrderResponse

//  {
//    "symbol": "BTCUSDT",
//    "orderId": 28,
//    "orderListId": -1, //Unless OCO, value will be -1
//    "clientOrderId": "6gCrw2kRUAF9CvJDGP16IP",
//    "transactTime": 1507725176595,
//    "price": "0.00000000",
//    "origQty": "10.00000000",
//    "executedQty": "10.00000000",
//    "cummulativeQuoteQty": "10.00000000",
//    "status": "FILLED",
//    "timeInForce": "GTC",
//    "type": "MARKET",
//    "side": "SELL",
//    "fills": [
//    {
//      "price": "4000.00000000",
//      "qty": "1.00000000",
//      "commission": "4.00000000",
//      "commissionAsset": "USDT"
//    },
//    {
//      "price": "3999.00000000",
//      "qty": "5.00000000",
//      "commission": "19.99500000",
//      "commissionAsset": "USDT"
//    },
//    {
//      "price": "3998.00000000",
//      "qty": "2.00000000",
//      "commission": "7.99600000",
//      "commissionAsset": "USDT"
//    },
//    {
//      "price": "3997.00000000",
//      "qty": "1.00000000",
//      "commission": "3.99700000",
//      "commissionAsset": "USDT"
//    },
//    {
//      "price": "3995.00000000",
//      "qty": "1.00000000",
//      "commission": "3.99500000",
//      "commissionAsset": "USDT"
//    }
//    ]
//  }

  case class NewOrderStdResponse(
                                  symbol: String,
                                  orderId: Long,
                                  clientOrderId: String,
                                  transactTime: Long,
                                  price: BigDecimal,
                                  origQty: BigDecimal,
                                  executedQty: BigDecimal,
                                  cumulativeQuoteQty: BigDecimal,
                                  orderStatus: OrderStatus,
                                  orderType: OrderType,
                                  orderSide: OrderSide,
                                  payload: String,
                                ) extends NewOrderResponse

  object NewOrderStdResponse {

    implicit val parser: RootJsonReader[NewOrderStdResponse] = { fromJson: JsValue =>
      fromJson.asJsObject("Invalid Order Response").getFields(
        "symbol",
        "orderId",
        "clientOrderId",
        "transactTime",
        "price",
        "origQty",
        "executedQty",
        "cumulativeQuoteQty",
        "orderStatus",
        "orderType",
        "orderSide",
      ) match {
        case Seq(
        JsString(symbol),
        JsNumber(_orderId),
        JsString(clientOrderId),
        JsNumber(_transactTime),
        JsString(_price),
        JsString(_origQty),
        JsString(_executedQty),
        JsString(_cumulativeQuoteQty),
        JsString(_orderStatus),
        JsString(_orderType),
        JsString(_orderSide)
        ) =>
          val orderId: Long = _orderId.toLongExact
          val transactTime: Long = _transactTime.toLongExact
          val price: BigDecimal = BigDecimal(_price)
          val origQty: BigDecimal = BigDecimal(_origQty)
          val executedQty: BigDecimal = BigDecimal(_executedQty)
          val cumulativeQuoteQty: BigDecimal = BigDecimal(_cumulativeQuoteQty)
          val orderStatus: OrderStatus = OrderStatus.valueOf(_orderStatus)
          val orderType: OrderType = OrderType.valueOf(_orderType)
          val orderSide: OrderSide = OrderSide.valueOf(_orderSide)
          NewOrderStdResponse(symbol,orderId,clientOrderId,transactTime,price,origQty,executedQty,cumulativeQuoteQty,orderStatus,orderType,orderSide, fromJson.prettyPrint)
        case _ => throw DeserializationException("Invalid Order Response!")
      }

    }
  }

}

