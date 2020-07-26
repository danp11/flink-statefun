package org.apache.flink.statefun.examples.stockmarket.surveillance;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;

import java.util.Collection;

public final class ProtobufSupplier extends BaseSupplier<MarketMessage> {

  public static BaseSupplier fromCollection(Collection<MarketMessage> messages) {
    return BaseSupplier.fromMessages(messages.iterator());
  }
}
