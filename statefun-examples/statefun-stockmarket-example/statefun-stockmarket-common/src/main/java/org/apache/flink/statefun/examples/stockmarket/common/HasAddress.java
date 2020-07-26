package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.Address;

import java.util.function.Function;

public interface HasAddress {

  Address getAddress(MarketMessage mm);

  static Function<MarketMessage, String> forParticipant() {
    return mm -> mm.getParticipant().getId();
  }

  static Function<MarketMessage, String> forInstrument() {
    return mm -> mm.getInstrument().getId();
  }

  static Function<MarketMessage, String> forStateChange() {
    return mm -> mm.getStateChange().getInstrumentId();
  }

  static Function<MarketMessage, String> forOrder() {
    return mm -> mm.getOrder().getInstrumentId() + "::" + mm.getOrder().getParticipantId();
  }

  static Function<MarketMessage, String> forTrade() {
    return mm -> mm.getTrade().getInstrumentId() + "::" + mm.getTrade().getParticipantId();
  }

  static Function<MarketMessage, String> forOrderOrTrade() {
    return mm -> mm.hasOrder() ? forOrder().apply(mm) : forTrade().apply(mm);
  }

  static Function<MarketMessage, String> forMarketAndTrade() {
    return (mm) -> mm.getMarket() + "::" + forTrade();
  }

  static Function<MarketMessage, String> forTradeAndSubClient() {
    return (mm) -> forTrade() + "::" + mm.getTrade().getSubClientId();
  }
}
