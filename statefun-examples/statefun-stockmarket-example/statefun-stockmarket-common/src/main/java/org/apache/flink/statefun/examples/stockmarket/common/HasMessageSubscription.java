package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Market;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

public interface HasMessageSubscription {

  ImmutableSet<MessageTypeCase> getValidMessageTypes();

  // Default-/Empty means "All markets"
  default ImmutableSet<Market> getValidMarkets() {
    return ImmutableSet.of();
  }

  default boolean isMessageSubscriber(MarketMessage mm) {
    return isValidMarket(mm) && isValidMessageType(mm);
  }

  default boolean isValidMarket(MarketMessage mm) {
    return getValidMarkets().isEmpty() || getValidMarkets().contains(mm.getMarket());
  }

  default boolean isValidMessageType(MarketMessage mm) {
    return getValidMessageTypes().contains(mm.getMessageTypeCase());
  }
}
