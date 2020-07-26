package org.apache.flink.statefun.examples.stockmarket.common.pubsub;

import org.apache.flink.statefun.examples.stockmarket.common.Fn;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.SubscribeMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.UnSubscribeMessage;
import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.Context;

public interface IsSubscriber<T extends Fn <T> & IsPublisher<T>> {

  default void subscribe(Context context, MarketMessage mm, T publishFunction) {
    send(context, mm, publishFunction, SubscribeMessage.getDefaultInstance());
  }

  default void unSubscribe(Context context, MarketMessage mm, T publishFunction) {
    send(context, mm, publishFunction, UnSubscribeMessage.getDefaultInstance());
  }

  default void send(Context context, MarketMessage mm, T publishFunction, Object obj) {
    Address defaultAddress = publishFunction.getAddress(mm);
    int saltId = defaultAddress.id().hashCode() % publishFunction.getNumPartitions();

    context.send(IsPublisher.createSaltedAddress(defaultAddress, saltId), obj);
  }
}
