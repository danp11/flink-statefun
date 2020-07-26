package org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Market;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.TRADE;
import static org.apache.flink.shaded.guava18.com.google.common.base.Suppliers.memoize;

public final class FnLargeTradeByMarketXSTO extends FnLargeTradeBase<FnLargeTradeByMarketXSTO> {

  public FnLargeTradeByMarketXSTO() {
    super(HasAddress.forMarketAndTrade(), TRADE);
  }

  @Override
  public ImmutableSet<Market> getValidMarkets() {
    return memoize(() -> ImmutableSet.of(Market.XSTO)).get();
  }
}
