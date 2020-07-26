package org.apache.flink.statefun.examples.stockmarket.common.pubsub;

import org.apache.flink.statefun.examples.stockmarket.common.Fn;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.SubscribeMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.UnSubscribeMessage;
import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.state.PersistedValue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface IsPublisher<T> {

  boolean isPublishMessage(MarketMessage message);

  void setPublishMessage(MarketMessage message);

  PersistedValue<T> getPublishMessage();

  int getNumPartitions();

  Set<Address> getSubscribers();

  // Todo
  default void addSubscriber(Context context, SubscribeMessage subscribeMessage) {
    if (subscribeMessage.getNumPartitions() != this.getNumPartitions()) {
      throw new RuntimeException(
          "Number of partitions for function "
              + context.caller().toString()
              + " must be set to "
              + this.getNumPartitions());
    }
    getSubscribers().add(context.caller());
  }

  default void publish(Context context, MarketMessage mm) {
    setPublishMessage(mm);

    for (Address address : getSubscribers()) {
      context.send(address, getPublishMessage().get());
    }
  }

  default void removeSubscriber(Context context, UnSubscribeMessage unSubscribeMessage) {
    getSubscribers().remove(context.caller());

    if (getSubscribers().isEmpty()) {
      logPublishingFinished();
    }

    getPublishMessage().clear();
  }

  static <T extends Fn & IsPublisher> List<Address> createSaltedAddresses(
      T publishFunction, MarketMessage mm) {
    Address defaultAddress = publishFunction.getAddress(mm);

    return IntStream.range(0, publishFunction.getNumPartitions())
        .mapToObj(i -> createSaltedAddress(defaultAddress, i))
        .collect(Collectors.toList());
  }

  static Address createSaltedAddress(Address defaultAddress, int i) {
    return new Address(defaultAddress.type(), defaultAddress.id() + "::" + i);
  }

  default void logPublishingFinished() {}
}
