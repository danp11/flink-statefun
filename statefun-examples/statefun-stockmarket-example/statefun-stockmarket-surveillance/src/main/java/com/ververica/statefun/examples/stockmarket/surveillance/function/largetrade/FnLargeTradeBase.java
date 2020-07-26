package org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.surveillance.FnSurveillanceBase;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.pubsub.IsStateChangeSubscriber;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.match.MatchBinder;

import java.util.function.Function;

class FnLargeTradeBase<F> extends FnSurveillanceBase<F> implements IsStateChangeSubscriber {
  protected FnLargeTradeBase(
      Function<MarketMessage, String> addressResolver,
      MarketMessage.MessageTypeCase... validMessageTypes) {
    super(addressResolver, validMessageTypes);
  }

  @Override
  public void configure(MatchBinder mb) {
    isTrade(mb, this::whenTrade);
    isInstrumentHalted(mb, this::whenInstrumentHalted);
    isInstrumentClosed(mb, this::whenInstrumentClosed);
    otherwise(mb, this::throwException);
  }

  void whenTrade(Context context, MarketMessage mm) {
    setUpSubscription(context, mm);
    // Todo
  }

  void whenInstrumentHalted(Context context, MarketMessage marketMessage) {
    // Todo
  }

  void whenInstrumentClosed(Context context, MarketMessage marketMessage) {
    // Todo

    unSubscribeToHaltedAndClosedMessages(context, marketMessage);
  }

  private void setUpSubscription(Context context, MarketMessage mm) {
    if (isFirstMessage(mm)) {
      subscribeToHaltedAndClosedMessages(context, mm);
    }
  }
}
