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

public final class FnInstrumentClosedPublisher
    extends FnSurveillanceBase<FnInstrumentClosedPublisher> implements IsPublisher<StateChange> {

  public FnInstrumentClosedPublisher() {
    super(HasAddress.forStateChange(), STATECHANGE);
  }

  @Persisted
  private final PersistedValue<StateChange> closed = PersistedValue.of("closed", StateChange.class);

  @Persisted
  private final PersistedValue<Set> listeners = PersistedValue.of("listeners", Set.class);

  public void configure(MatchBinder mb) {
    isSubscribeMessage(mb, this::addSubscriber);
    isInstrumentClosed(mb, this::publish);
    isUnSubscribeMessage(mb, this::removeSubscriber);
    otherwise(mb, this::throwException);
  }

  @Override
  public boolean isPublishMessage(MarketMessage message) {
    return message.getStateChange().isClosed();
  }

  @Override
  public void logPublishingFinished() {
    logger().info("End of day completed for instrument " + closed.get().getInstrumentId());
  }

  @Override
  public void setPublishMessage(MarketMessage message) {
    closed.set(message.getStateChange());
  }

  @Override
  public PersistedValue<StateChange> getPublishMessage() {
    return closed;
  }

  @Override
  public int getNumPartitions() {
    return 1000;
  }

  @Override
  public Set<Address> getSubscribers() {
    return listeners.getOrDefault(Sets.newHashSet());
  }
}
