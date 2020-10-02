package com.binance.api.client.domain.general

import spray.json.{DeserializationException, JsObject, JsString, JsValue, RootJsonReader}

/**
  * Filters define trading rules on a symbol or an exchange. Filters come in two forms: symbol filters and exchange filters.
  *
  * The PRICE_FILTER defines the price rules for a symbol.
  *
  * The LOT_SIZE filter defines the quantity (aka "lots" in auction terms) rules for a symbol.
  *
  * The MIN_NOTIONAL filter defines the minimum notional value allowed for an order on a symbol. An order's notional value is the price * quantity.
  *
  * The MAX_NUM_ORDERS filter defines the maximum number of orders an account is allowed to have open on a symbol. Note that both "algo" orders and normal orders are counted for this filter.
  *
  * The MAX_ALGO_ORDERS filter defines the maximum number of "algo" orders an account is allowed to have open on a symbol. "Algo" orders are STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
  */

sealed trait SymbolFilter {

  def filterType: FilterType
}

object SymbolFilter {


  /**
   *
   * @param minPrice Defines the maximum price/stopPrice allowed.
   * @param maxPrice Defines the maximum price/stopPrice allowed.
   * @param tickSize Defines the intervals that a price/stopPrice can be increased/decreased by.
   */
  case class PriceFilter(minPrice: BigDecimal, maxPrice: BigDecimal, tickSize: BigDecimal) extends SymbolFilter{
    override val filterType: FilterType = FilterType.PRICE_FILTER

    def validated(price: BigDecimal): BigDecimal =
      BigDecimal((price.quot(tickSize) * tickSize).bigDecimal.stripTrailingZeros())

    def validatedOption(price: BigDecimal): Option[BigDecimal] =
      Some(validated(price)).filter(isValid)

    def isValid(value: BigDecimal): Boolean = value >= minPrice && value <= maxPrice

  }

  object PriceFilter {
    implicit val parser: RootJsonReader[PriceFilter] = {fromJson: JsValue =>
      fromJson.asJsObject("Invalid price filter").getFields("minPrice", "maxPrice", "tickSize") match {
        case Seq(JsString(_minPrice), JsString(_maxPrice), JsString(_tickSize)) =>
          PriceFilter(
            BigDecimal(_minPrice),
            BigDecimal(_maxPrice),
            BigDecimal(_tickSize),
          )
        case _ => throw DeserializationException("Invalid lot size filter")
      }
    }
  }



  /**
   *
   * @param minQty Defines the minimum quantity/icebergQty allowed.
   * @param maxQty Defines the maximum quantity/icebergQty allowed.
   * @param stepSize Defines the intervals that a quantity/icebergQty can be increased/decreased by.
   */
  case class LotSizeFilter(minQty: BigDecimal, maxQty: BigDecimal, stepSize: BigDecimal) extends SymbolFilter{

    override val filterType: FilterType = FilterType.PRICE_FILTER

    def validated(value: BigDecimal): BigDecimal =
      BigDecimal((value.quot(stepSize) * stepSize).bigDecimal.stripTrailingZeros())

    def absValidated(value: BigDecimal): BigDecimal =
      BigDecimal((value.abs.quot(stepSize) * stepSize).bigDecimal.stripTrailingZeros())

    def validatedOption(value: BigDecimal): Option[BigDecimal] =
      Some(validated(value)).filter(value => isValid(value.abs))

    def absValidatedOption(value: BigDecimal): Option[BigDecimal] =
      Some(absValidated(value)).filter(isValid)


    def isValid(value: BigDecimal): Boolean = value >= minQty && value <= maxQty
  }

  object LotSizeFilter {
    implicit val parser: RootJsonReader[LotSizeFilter] = {fromJson: JsValue =>
      fromJson.asJsObject("Invalid lot size filter").getFields("minQty", "maxQty", "stepSize") match {
        case Seq(JsString(_minQty), JsString(_maxQty), JsString(_stepSize)) =>
          LotSizeFilter(
            BigDecimal(_minQty),
            BigDecimal(_maxQty),
            BigDecimal(_stepSize).round(),
          )
        case _ => throw DeserializationException("Invalid lot size filter")
      }
    }
  }

  case object Other extends SymbolFilter{
    override val filterType: FilterType = FilterType.MAX_NUM_ORDERS
  }

  implicit val parser: RootJsonReader[SymbolFilter] = { fromJson =>
    val obj: JsObject = fromJson.asJsObject("Invalid Symbol Filter")
    val filterType: FilterType = obj.getFields("filterType") match {
      case Seq(JsString(_filterType)) => FilterType.valueOf(_filterType)
      case _ => throw DeserializationException("Invalid Symbol Filter!")
    }
    filterType match {
      case FilterType.LOT_SIZE => LotSizeFilter.parser.read(obj)
      case FilterType.PRICE_FILTER => PriceFilter.parser.read(obj)
      case _ => Other
    }
  }
}
