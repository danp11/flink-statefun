package org.apache.flink.statefun.examples.stockmarket.surveillance.function.cache;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Participant;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.ParticipantResponse;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.RequestMessage;
import org.apache.flink.statefun.sdk.Context;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.PARTICIPANT;

public final class FnCacheParticipant extends FnCacheBase<FnCacheParticipant, Participant> {

  public FnCacheParticipant() {
    super(HasAddress.forParticipant(), PARTICIPANT);
  }

  @Override
  public void whenMarketMessage(Context context, MarketMessage mm) {
    super.set(mm.getParticipant());
  }

  @Override
  public void whenRequestMessage(Context context, RequestMessage rm) {
    context.reply(ParticipantResponse.newBuilder().setParticipant(super.get()));
  }
}
