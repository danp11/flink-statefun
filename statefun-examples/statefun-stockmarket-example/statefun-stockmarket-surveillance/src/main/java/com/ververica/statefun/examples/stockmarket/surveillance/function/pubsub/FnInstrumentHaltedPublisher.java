package org.apache.flink.statefun.examples.stockmarket.surveillance.function.pubsub;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.common.pubsub.IsPublisher;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.StateChange;
import org.apache.flink.statefun.examples.stockmarket.surveillance.FnSurveillanceBase;
import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.match.MatchBinder;
import org.apache.flink.statefun.sdk.state.PersistedValue;
import org.apache.commons.compress.utils.Sets;

import java.util.Set;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.marketmessage.MarketMessage.MessageTypeCase.STATECHANGE;

public final class FnInstrumentHaltedPublisher
    extends FnSurveillanceBase<FnInstrumentHaltedPublisher> implements IsPublisher<StateChange> {

  public FnInstrumentHaltedPublisher() {
    super(HasAddress.forStateChange(), STATECHANGE);
  }

  @Persisted
  private final PersistedValue<StateChange> halted = PersistedValue.of("halted", StateChange.class);

  @Persisted
  private final PersistedValue<Set> listeners = PersistedValue.of("listeners", Set.class);

  public void configure(MatchBinder mb) {
    isSubscribeMessage(mb, this::addSubscriber);
    isInstrumentHalted(mb, this::publish);
    isUnSubscribeMessage(mb, this::removeSubscriber);
    otherwise(mb, this::throwException);
  }

  @Override
  public boolean isPublishMessage(MarketMessage message) {
    return message.hasStateChange() && message.getStateChange().isHalted();
  }

  @Override
  public void setPublishMessage(MarketMessage message) {
    halted.set(message.getStateChange());
  }

  @Override
  public PersistedValue<StateChange> getPublishMessage() {
    return halted;
  }

  @Override
  public int getNumPartitions() {
    return 100;
  }

  @Override
  public Set<Address> getSubscribers() {
    return listeners.getOrDefault(Sets.newHashSet());
  }
}
