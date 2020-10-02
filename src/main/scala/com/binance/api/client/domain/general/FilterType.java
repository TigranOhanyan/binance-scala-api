package com.binance.api.client.domain.general;

/**
 * Filters define trading rules on a symbol or an exchange. Filters come in two forms: symbol filters and exchange filters.
 */
public enum FilterType {
  // Symbol
  PRICE_FILTER,
  PERCENT_PRICE,
  LOT_SIZE,
  MIN_NOTIONAL,
  ICEBERG_PARTS,
  MARKET_LOT_SIZE,
  MAX_NUM_ORDERS,
  MAX_NUM_ALGO_ORDERS,
  MAX_NUM_ICEBERG_ORDERS,
  MAX_POSITION,
  // Exchange
  EXCHANGE_MAX_NUM_ORDERS,
  EXCHANGE_MAX_ALGO_ORDERS
}


