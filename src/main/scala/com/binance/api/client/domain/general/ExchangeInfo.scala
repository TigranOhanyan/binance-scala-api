package com.binance.api.client.domain.general

import spray.json.{DeserializationException, JsArray, JsNumber, JsString, JsValue, RootJsonFormat, RootJsonReader}

/**
  * Current exchange trading rules and symbol information.
  * https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
  */
case class ExchangeInfo(
    timezone:   String,
    serverTime: Long,
    rateLimits: List[RateLimit],
    symbols:    List[SymbolInfo],
    exchangeFilters: List[ExchangeFilter]
) {
  lazy val getSymbolInfo: Map[String, SymbolInfo] =
    symbols.map(s => s.symbol -> s).toMap

}

object ExchangeInfo {

  implicit val parser: RootJsonReader[ExchangeInfo] = { fromJson: JsValue =>
    fromJson.asJsObject("Invalid Exchange Info!") getFields ("timezone", "serverTime", "symbols") match {
      case Seq(JsString(timezone), JsNumber(_serverTime), JsArray(_symbols)) =>
        val serverTime = _serverTime.toLongExact
        val symbols = _symbols.map(SymbolInfo.parser.read).toList
        ExchangeInfo(timezone, serverTime, List.empty[RateLimit], symbols, List.empty[ExchangeFilter])
      case _ => throw DeserializationException("Invalid Exchange Info!")
    }

  }
}