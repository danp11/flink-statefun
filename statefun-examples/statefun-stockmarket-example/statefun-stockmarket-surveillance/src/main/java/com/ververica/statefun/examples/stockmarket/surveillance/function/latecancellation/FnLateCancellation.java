package org.apache.flink.statefun.examples.stockmarket.surveillance.function.latecancellation;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.surveillance.FnSurveillanceBase;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.match.MatchBinder;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.ORDER;
import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.TRADE;

public final class FnLateCancellation extends FnSurveillanceBase<FnLateCancellation> {

  public FnLateCancellation() {
    super(HasAddress.forOrderOrTrade(), ORDER, TRADE);
  }

  @Override
  public void configure(MatchBinder mb) {
    isTrade(mb, this::whenTrade);
    isOrder(mb, this::whenOrder);
    otherwise(mb, this::throwException);
  }

  private void whenOrder(Context context, MarketMessage mm) {
    // Todo
  }

  private void whenTrade(Context context, MarketMessage mm) {
    // Todo
  }
}
