package com.binance.api.client.domain.general

import spray.json.{DeserializationException, JsNumber, RootJsonReader}

/**
  * Time of the server running Binance's REST API.
  */
case class ServerTime(serverTime: Long)

object ServerTime {
   implicit val parser: RootJsonReader[ServerTime] = { fromJSon =>
     fromJSon.asJsObject("Invalid Server Time!").getFields("serverTime") match {
        case Seq(JsNumber(_serverTime)) => ServerTime(_serverTime.toLongExact)
        case _ => throw DeserializationException("Invalid Server Time!")
     }
   }
}
