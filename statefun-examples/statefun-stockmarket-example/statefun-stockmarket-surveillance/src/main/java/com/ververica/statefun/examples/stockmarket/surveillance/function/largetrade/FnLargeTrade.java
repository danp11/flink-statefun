package org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.TRADE;

public final class FnLargeTrade extends FnLargeTradeBase<FnLargeTrade> {
  public FnLargeTrade() {
    super(HasAddress.forTrade(), TRADE);
  }
}
