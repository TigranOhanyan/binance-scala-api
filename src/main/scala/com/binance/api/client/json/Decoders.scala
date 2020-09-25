package com.binance.api.client.json

import com.binance.api.client.domain.account._
import com.binance.api.client.domain.event._
import com.binance.api.client.domain._
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderAckResponse
import com.binance.api.client.domain.general._
import com.binance.api.client.domain.market._
import io.circe.{Decoder, DecodingFailure, HCursor, JsonObject}
import io.circe.generic.semiauto.deriveDecoder

object Decoders {
  /* Manually derive typed primitives */
  implicit lazy val AmountDecoder:    Decoder[Amount]    = Decoder.decodeBigDecimal.map(Amount.apply)
  implicit lazy val AssetDecoder:     Decoder[Asset]     = Decoder.decodeString.map(Asset.apply)
  implicit lazy val InstantDecoder:   Decoder[Instant]   = Decoder.decodeLong.map(Instant.apply)
  implicit lazy val OrderIdDecoder:   Decoder[OrderId]   = Decoder.decodeLong.map(OrderId.apply)
  implicit lazy val PercentDecoder:   Decoder[Percent]   = Decoder.decodeBigDecimal.map(Percent.apply)
  implicit lazy val PriceDecoder:     Decoder[Price]     = Decoder.decodeBigDecimal.map(Price.apply)
  implicit lazy val PriceDiffDecoder: Decoder[PriceDiff] = Decoder.decodeBigDecimal.map(PriceDiff.apply)
  implicit lazy val QuantityDecoder:  Decoder[Quantity]  = Decoder.decodeBigDecimal.map(Quantity.apply)
  implicit lazy val SymbolDecoder:    Decoder[Symbol]    = Decoder.decodeString.map(Symbol.apply)
  implicit lazy val VolumeDecoder:    Decoder[Volume]    = Decoder.decodeBigDecimal.map(Volume.apply)

  /* java enums */
  implicit lazy val CandlestickIntervalDecoder: Decoder[CandlestickInterval] = EnumDecoder(CandlestickInterval.values())
  implicit lazy val ExecutionTypeDecoder:       Decoder[ExecutionType]       = EnumDecoder(ExecutionType.values())
  implicit lazy val FilterTypeDecoder:          Decoder[FilterType]          = EnumDecoder(FilterType.values)
  implicit lazy val OrderRejectReasonDecoder:   Decoder[OrderRejectReason]   = EnumDecoder(OrderRejectReason.values())
  implicit lazy val OrderSideDecoder:           Decoder[OrderSide]           = EnumDecoder(OrderSide.values())
  implicit lazy val OrderStatusDecoder:         Decoder[OrderStatus]         = EnumDecoder(OrderStatus.values())
  implicit lazy val OrderTypeDecoder:           Decoder[OrderType]           = EnumDecoder(OrderType.values())
  implicit lazy val RateLimitIntervalDecoder:   Decoder[RateLimitInterval]   = EnumDecoder(RateLimitInterval.values)
  implicit lazy val RateLimitTypeDecoder:       Decoder[RateLimitType]       = EnumDecoder(RateLimitType.values)
  implicit lazy val SymbolStatusDecoder:        Decoder[SymbolStatus]        = EnumDecoder(SymbolStatus.values)
  implicit lazy val TimeInForceDecoder:         Decoder[TimeInForce]         = EnumDecoder(TimeInForce.values())

  /* these exactly correspond to their schema, so we can auto-derive them */
  implicit lazy val AccountDecoder:          Decoder[Account]          = deriveDecoder[Account]
  implicit lazy val AssetBalanceDecoder:     Decoder[AssetBalance]     = deriveDecoder[AssetBalance]
  implicit lazy val BookTickerDecoder:       Decoder[BookTicker]       = deriveDecoder[BookTicker]
  implicit lazy val DepositAddressDecoder:   Decoder[DepositAddress]   = deriveDecoder[DepositAddress]
  implicit lazy val DepositDecoder:          Decoder[Deposit]          = deriveDecoder[Deposit]
  implicit lazy val DepositHistoryDecoder:   Decoder[DepositHistory]   = deriveDecoder[DepositHistory]
  implicit lazy val ExchangeFilterDecoder:   Decoder[ExchangeFilter]   = deriveDecoder[ExchangeFilter]
  implicit lazy val ExchangeInfoDecoder:     Decoder[ExchangeInfo]     = deriveDecoder[ExchangeInfo]
  implicit lazy val ListenKeyDecoder:        Decoder[ListenKey]        = deriveDecoder[ListenKey]
  implicit lazy val MyTradeDecoder:          Decoder[MyTrade]          = deriveDecoder[MyTrade]
  implicit lazy val NewOrderResponseDecoder: Decoder[NewOrderAckResponse] = deriveDecoder[NewOrderAckResponse]
  implicit lazy val OrderBookDecoder:        Decoder[OrderBook]        = deriveDecoder[OrderBook]
  implicit lazy val OrderDecoder:            Decoder[Order]            = deriveDecoder[Order]
  implicit lazy val RateLimitDecoder:        Decoder[RateLimit]        = deriveDecoder[RateLimit]
  implicit lazy val ServerTimeDecoder:       Decoder[ServerTime]       = deriveDecoder[ServerTime]
  implicit lazy val SymbolFilterDecoder:     Decoder[SymbolFilter]     = deriveDecoder[SymbolFilter]
  implicit lazy val SymbolInfoDecoder:       Decoder[SymbolInfo]       = deriveDecoder[SymbolInfo]
  implicit lazy val TickerPriceDecoder:      Decoder[TickerPrice]      = deriveDecoder[TickerPrice]
  implicit lazy val TickerStatisticsDecoder: Decoder[TickerStatistics] = deriveDecoder[TickerStatistics]
  implicit lazy val WithdrawDecoder:         Decoder[Withdraw]         = deriveDecoder[Withdraw]
  implicit lazy val WithdrawHistoryDecoder:  Decoder[WithdrawHistory]  = deriveDecoder[WithdrawHistory]

  /* The rest have short names which we want to rename, or arrives as a tuple */

  implicit lazy val OrderBookEntryDecoder: Decoder[OrderBookEntry] =
    Decoder.decodeTuple3[Price, Quantity, Seq[String]].map {
      case (a0, a1, _) => OrderBookEntry(a0, a1)
    }

  implicit lazy val AccountUpdateEventDecoder: Decoder[AccountUpdateEvent] = {
    implicit val AssetBalanceDecoder: Decoder[AssetBalance] =
      Decoder.forProduct3("a", "f", "l")(AssetBalance.apply)

    Decoder.forProduct3("e", "E", "B")(AccountUpdateEvent.apply)
  }

  implicit lazy val CandlestickDetailedDecoder: Decoder[CandlestickDetailed] =
    Decoder.forProduct15("t", "o", "h", "l", "c", "v", "T", "i", "f", "L", "q", "n", "V", "Q", "x")(
      CandlestickDetailed.apply
    )

  implicit lazy val CandlestickEventDecoder: Decoder[CandlestickEvent] =
    Decoder.forProduct4("e", "E", "s", "k")(CandlestickEvent.apply)

  implicit lazy val OrderTradeUpdateEventDecoder: Decoder[OrderTradeUpdateEvent] =
    Decoder.forProduct20("e",
                         "E",
                         "s",
                         "c",
                         "S",
                         "o",
                         "f",
                         "q",
                         "p",
                         "x",
                         "X",
                         "r",
                         "i",
                         "l",
                         "z",
                         "L",
                         "n",
                         "N",
                         "T",
                         "t")(OrderTradeUpdateEvent.apply)

  implicit lazy val AggTradeEventDecoder: Decoder[AggTradeEvent] =
    Decoder.forProduct11("e", "E", "s", "a", "p", "q", "f", "l", "T", "m", "M")(AggTradeEvent.apply)

  implicit lazy val TradeEventDecoder: Decoder[TradeEvent] =
    Decoder.forProduct11("e", "E", "s", "t", "p", "q", "b", "a", "T", "m", "M")(TradeEvent.apply)

  implicit lazy val DepthEventDecoder: Decoder[DepthEvent] =
    Decoder.forProduct7("e", "E", "s", "U", "u", "b", "a")(DepthEvent.apply)

  implicit lazy val AggTradeDecoder: Decoder[AggTrade] =
    Decoder.forProduct8("a", "p", "q", "f", "l", "T", "m", "M")(AggTrade.apply)

  implicit lazy val TradeDecoder: Decoder[Trade] =
    Decoder.forProduct6("id", "price", "qty", "time", "isBuyerMaker", "isBestMatch")(Trade.apply)

  implicit lazy val CandlestickDecoder: Decoder[Candlestick] =
    Decoder
      .decodeTuple12[Instant, Price, Price, Price, Price, Volume, Instant, String, Long, String, String, String]
      .map {
        case (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, _) =>
          Candlestick(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
      }

  implicit lazy val UserDataUpdateEventDecoder: Decoder[Either[OrderTradeUpdateEvent, AccountUpdateEvent]] =
    Decoder.decodeJsonObject.flatMap { (obj: JsonObject) =>
      obj("e").flatMap(_.asString) match {
        case Some("executionReport") =>
          OrderTradeUpdateEventDecoder.map(Left.apply)
        case Some("outboundAccountInfo") =>
          AccountUpdateEventDecoder.map(Right.apply)
        case _ =>
          (c: HCursor) =>
            Left(DecodingFailure("Not a valid UserDataUpdateEvent", c.history))
      }
    }
}
