package com.binance.api.client.domain.general

import com.binance.api.client.domain.{Asset, OrderType, Symbol}
import spray.json.{DeserializationException, JsArray, JsBoolean, JsNumber, JsString, JsValue, RootJsonReader}

/**
  * Symbol information (base/quote).
  */
case class SymbolInfo(symbol:             String,
                      status:             SymbolStatus,
                      baseAsset:          String,
                      baseAssetPrecision: Int,
                      quoteAsset:         String,
                      quotePrecision:     Int,
                      orderTypes:         List[OrderType],
                      icebergAllowed:     Boolean,
                      filters:            List[SymbolFilter]
                     ) {

  /**
    * @return symbol filter information for the provided filter type.
    */
  lazy val getSymbolFilter: Map[FilterType, SymbolFilter] =
    filters.map(f => f.filterType -> f).toMap
}

object SymbolInfo {

  implicit val parser: RootJsonReader[SymbolInfo] = { fromJson: JsValue =>
    fromJson.asJsObject("Invalid Symbol Info!").getFields(
      "symbol",
      "status",
      "baseAsset",
      "baseAssetPrecision",
      "quoteAsset",
      "quotePrecision",
      "orderTypes",
      "icebergAllowed",
      "filters"
    ) match {
      case Seq(JsString(symbol), JsString(_status), JsString(baseAsset), JsNumber(baseAssetPrecision), JsString(quoteAsset), JsNumber(quoteAssetPrecision), JsArray(_orderTypes), JsBoolean(iceberg), JsArray(_filters)) =>
        val orderTypes: List[OrderType] = _orderTypes.map(_.asInstanceOf[JsString]).map(js => OrderType.valueOf(js.value)).toList
        val filters: List[SymbolFilter] = _filters.map(SymbolFilter.parser.read).toList.filterNot(_ == SymbolFilter.Other)
        SymbolInfo(
          symbol,
          SymbolStatus.valueOf(_status),
          baseAsset,
          baseAssetPrecision.toIntExact,
          quoteAsset,
          quoteAssetPrecision.toIntExact,
          orderTypes,
          iceberg,
          filters
        )
      case _ => throw DeserializationException("Invalid Symbol Info!")
    }
  }
}