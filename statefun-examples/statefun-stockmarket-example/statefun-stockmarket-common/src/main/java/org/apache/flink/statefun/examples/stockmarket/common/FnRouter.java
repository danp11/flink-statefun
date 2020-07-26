package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.common.pubsub.IsPublisher;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.io.Router;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;

final class FnRouter implements Router<MarketMessage> {

  private ImmutableSet<Fn> functions;

  FnRouter(Set<Fn> functions) {
    Objects.requireNonNull(functions);
    this.functions = ImmutableSet.copyOf(functions);
  }

  @Override
  public void route(MarketMessage mm, Downstream<MarketMessage> downstream) {
    for (Fn function : functions)
      if (function.isMessageSubscriber(mm)) {
        if (isPublishingMessage(function, mm)) {
          sendBatch(function, mm, downstream);
        } else {
          sendSingle(function, mm, downstream);
        }
      }
  }

  private boolean isPublishingMessage(Fn function, MarketMessage mm) {
    return function instanceof IsPublisher && ((IsPublisher) function).isPublishMessage(mm);
  }

  private void sendBatch(Fn function, MarketMessage mm, Downstream<MarketMessage> downstream) {
    List<Address> saltedAddresses = IsPublisher.createSaltedAddresses(((IsPublisher & Fn) function), mm);

    saltedAddresses.forEach(address -> sendDownstream(address, mm, downstream));
  }

  private void sendSingle(Fn function, MarketMessage mm, Downstream<MarketMessage> downstream) {
    sendDownstream(function.getAddress(mm), mm, downstream);
  }

  private void sendDownstream(
      Address address, MarketMessage mm, Downstream<MarketMessage> downstream) {
    downstream.forward(address, mm);
  }
}
