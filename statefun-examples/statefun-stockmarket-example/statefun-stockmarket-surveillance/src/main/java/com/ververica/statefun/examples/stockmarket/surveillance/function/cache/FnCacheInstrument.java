package org.apache.flink.statefun.examples.stockmarket.surveillance.function.cache;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Instrument;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.InstrumentResponse;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.RequestMessage;
import org.apache.flink.statefun.sdk.Context;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.INSTRUMENT;

public final class FnCacheInstrument extends FnCacheBase<FnCacheInstrument, Instrument> {
  public FnCacheInstrument() {
    super(HasAddress.forInstrument(), INSTRUMENT);
  }

  @Override
  public void whenMarketMessage(Context context, MarketMessage mm) {
    System.out.println(mm.getInstrument().getName());
    super.set(mm.getInstrument());
  }

  @Override
  public void whenRequestMessage(Context context, RequestMessage rm) {
    context.reply(InstrumentResponse.newBuilder().setInstrument(super.get()));
  }
}
