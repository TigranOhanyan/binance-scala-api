package com.binance.api.client.exception

import com.binance.api.client.BinanceApiError

final case class BinanceApiExceptionError(error: BinanceApiError) extends RuntimeException(error.msg)

object BinanceApiExceptionError {
  def apply(code: Int, message: String): BinanceApiExceptionError =
    BinanceApiExceptionError(BinanceApiError(code, message))
}

final case class BinanceApiExceptionMsg(value:   String)          extends RuntimeException(value)
final case class BinanceApiExceptionCause(cause: Throwable)       extends RuntimeException(cause)
