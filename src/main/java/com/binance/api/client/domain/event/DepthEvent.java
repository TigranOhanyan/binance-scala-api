package com.binance.api.client.domain.event;

import com.binance.api.client.domain.market.OrderBookEntry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Depth delta event for a symbol.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepthEvent {
  @JsonCreator
  public DepthEvent(@JsonProperty("e") String eventType,
                    @JsonProperty("E") long eventTime,
                    @JsonProperty("s") String symbol,
                    @JsonProperty("u") long updateId,
                    @JsonProperty("b") List<OrderBookEntry> bids,
                    @JsonProperty("a") List<OrderBookEntry> asks) {
    this.eventType = eventType;
    this.eventTime = eventTime;
    this.symbol = symbol;
    this.updateId = updateId;
    this.bids = bids;
    this.asks = asks;
  }

  private final String eventType;

  private final long eventTime;

  private final String symbol;

  /**
   * updateId to sync up with updateid in /api/v1/depth
   */
  private final long updateId;

  /**
   * Bid depth delta.
   */
  private final List<OrderBookEntry> bids;

  /**
   * Ask depth delta.
   */
  private final List<OrderBookEntry> asks;

  public String getEventType() {
    return eventType;
  }

  public long getEventTime() {
    return eventTime;
  }

  public String getSymbol() {
    return symbol;
  }

  public long getUpdateId() {
    return updateId;
  }

  public List<OrderBookEntry> getBids() {
    return bids;
  }

  public List<OrderBookEntry> getAsks() {
    return asks;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("eventType", eventType)
        .append("eventTime", eventTime)
        .append("symbol", symbol)
        .append("updateId", updateId)
        .append("bids", bids)
        .append("asks", asks)
        .toString();
  }
}
