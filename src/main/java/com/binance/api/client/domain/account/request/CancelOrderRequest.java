package com.binance.api.client.domain.account.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

/**
 * Request object for canceling an order.
 */
public class CancelOrderRequest extends OrderRequest {

  @JsonCreator
  public CancelOrderRequest(@JsonProperty("symbol") String symbol,
                            @JsonProperty("recvWindow") Optional<Long> recvWindow,
                            @JsonProperty("timestamp") Optional<Long> timestamp,
                            @JsonProperty("orderId") Optional<Long> orderId,
                            @JsonProperty("origClientOrderId") Optional<String> origClientOrderId,
                            @JsonProperty("newClientOrderId") Optional<String> newClientOrderId) {
    super(symbol, recvWindow, timestamp);
    this.orderId = orderId;
    this.origClientOrderId = origClientOrderId;
    this.newClientOrderId = newClientOrderId;
  }

  public CancelOrderRequest(String symbol, Long orderId) {
    super(symbol);
    this.orderId = Optional.of(orderId);
    origClientOrderId = Optional.empty();
    newClientOrderId = Optional.empty();
  }

  private final Optional<Long> orderId;

  private final Optional<String> origClientOrderId;

  /**
   * Used to uniquely identify this cancel. Automatically generated by default.
   */
  private final Optional<String> newClientOrderId;

  public Optional<Long> getOrderId() {
    return orderId;
  }

  public Optional<String> getOrigClientOrderId() {
    return origClientOrderId;
  }

  public Optional<String> getNewClientOrderId() {
    return newClientOrderId;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("orderId", orderId)
        .append("origClientOrderId", origClientOrderId)
        .append("newClientOrderId", newClientOrderId)
        .toString();
  }
}
