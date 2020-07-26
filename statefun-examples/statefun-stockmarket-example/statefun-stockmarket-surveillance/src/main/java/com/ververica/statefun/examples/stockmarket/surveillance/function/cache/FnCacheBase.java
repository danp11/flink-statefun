package org.apache.flink.statefun.examples.stockmarket.surveillance.function.cache;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.RequestMessage;
import org.apache.flink.statefun.examples.stockmarket.surveillance.FnSurveillanceBase;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.match.MatchBinder;
import org.apache.flink.statefun.sdk.state.PersistedValue;

import java.util.Optional;
import java.util.function.Function;

abstract class FnCacheBase<F, T> extends FnSurveillanceBase<F> {

  @Persisted
  private final PersistedValue<Object> cached = PersistedValue.of("cached", Object.class);

  protected FnCacheBase(
      Function<MarketMessage, String> addressResolver,
      MarketMessage.MessageTypeCase... validMessageTypes) {
    super(addressResolver, validMessageTypes);
  }

  @Override
  public void configure(MatchBinder mb) {
    isMarketMessage(mb, this::whenMarketMessage);
    isRequestMessage(mb, this::whenRequestMessage);
    otherwise(mb, this::throwException);
  }

  abstract void whenMarketMessage(Context context, MarketMessage mm);

  abstract void whenRequestMessage(Context context, RequestMessage rm);

  protected T get() {
    return (T) cached.get();
  }

  protected Optional<T> find() {
    return Optional.ofNullable((T) cached.getOrDefault(null));
  }

  protected void set(T object) {
    cached.set(object);
  }

  protected void set(T object, String timeToLive) { // Todo
    cached.set(object);
  }
}
