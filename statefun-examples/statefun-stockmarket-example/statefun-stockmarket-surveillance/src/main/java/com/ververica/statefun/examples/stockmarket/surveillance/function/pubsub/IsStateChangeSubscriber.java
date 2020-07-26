package org.apache.flink.statefun.examples.stockmarket.surveillance.function.pubsub;

import org.apache.flink.statefun.examples.stockmarket.common.pubsub.IsSubscriber;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.Context;

public interface IsStateChangeSubscriber extends IsSubscriber {

  default void unSubscribeToHaltedAndClosedMessages(Context context, MarketMessage mm) {
    unSubscribeToHaltedMessage(context, mm);
    unSubscribeToClosedMessage(context, mm);
  }

  default void subscribeToHaltedAndClosedMessages(Context context, MarketMessage mm) {
    subscribeToHaltedMessage(context, mm);
    subscribeToClosedMessage(context, mm);
  }

  default void subscribeToHaltedMessage(Context context, MarketMessage mm) {
    subscribe(context, mm, new FnInstrumentHaltedPublisher());
  }

  default void subscribeToClosedMessage(Context context, MarketMessage mm) {
    subscribe(context, mm, new FnInstrumentClosedPublisher());
  }

  default void unSubscribeToHaltedMessage(Context context, MarketMessage mm) {
    unSubscribe(context, mm, new FnInstrumentHaltedPublisher());
  }

  default void unSubscribeToClosedMessage(Context context, MarketMessage mm) {
    unSubscribe(context, mm, new FnInstrumentClosedPublisher());
  }
}
