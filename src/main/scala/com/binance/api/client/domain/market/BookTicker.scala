package com.binance.api.client.domain.market

import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.binance.api.client.domain.{Price, Quantity, Symbol}
import spray.json.{DeserializationException, JsArray, JsString, JsValue, JsonReader, RootJsonReader}

/**
  * Represents the best price/qty on the order book for a given symbol.
 * @param symbol Ticker symbol.
 * @param bidPrice Bid price.
 * @param bidQty Bid quantity.
 * @param askPrice Ask price.
 * @param askQty Ask quantity.
  */
case class BookTicker(
    symbol: String,
    bidPrice: BigDecimal,
    bidQty: BigDecimal,
    askPrice: BigDecimal,
    askQty: BigDecimal
)

object BookTicker {
  implicit val parser: RootJsonReader[BookTicker] = { fromJson: JsValue =>
    fromJson.asJsObject("Invalid All Book Ticker!") getFields("symbol","bidPrice", "bidQty", "askPrice", "askQty") match {
      case Seq(JsString(symbol),JsString(_bidPrice),JsString(_bidQty),JsString(_askPrice),JsString(_askQty)) =>
        BookTicker(symbol,BigDecimal(_bidPrice),BigDecimal(_bidQty),BigDecimal(_askPrice),BigDecimal(_askQty))
      case _ => throw DeserializationException("Invalid Book ticker!")
    }
  }
}

case class AllBookTicker(tickers: Seq[BookTicker])

object AllBookTicker{

  implicit val parser: RootJsonReader[AllBookTicker] = {
    case JsArray(elements) => AllBookTicker(elements.map(BookTicker.parser.read))
    case _ => throw DeserializationException("Invalid All Book Ticker!")
  }
}
