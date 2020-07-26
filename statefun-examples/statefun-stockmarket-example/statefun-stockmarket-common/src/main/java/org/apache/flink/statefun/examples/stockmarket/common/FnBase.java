package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase;
import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.match.StatefulMatchFunction;
import org.apache.flink.statefun.sdk.state.PersistedValue;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

public abstract class FnBase<F> extends StatefulMatchFunction implements Fn<F> {

  @Persisted
  private final PersistedValue<FunctionType> type = PersistedValue.of("type", FunctionType.class);

  @Persisted
  private final PersistedValue<Function> addressResolver =
      PersistedValue.of("addressResolver", Function.class);

  @Persisted
  private final PersistedValue<ImmutableSet> validMessageTypes =
      PersistedValue.of("validMessageTypes", ImmutableSet.class);

  @Persisted
  private final PersistedValue<Logger> logger = PersistedValue.of("logger", Logger.class);

  @Persisted
  private final PersistedValue<MarketMessage> firstMessage =
      PersistedValue.of("firstMessage", MarketMessage.class);

  protected FnBase(
      Function<MarketMessage, String> addressResolver, MessageTypeCase... validMessageTypes) {
    Objects.requireNonNull(addressResolver);
    Objects.requireNonNull(validMessageTypes);

    this.type.set(createFunctionType());
    this.addressResolver.set(addressResolver);
    this.validMessageTypes.set(ImmutableSet.copyOf(validMessageTypes));
  }

  protected Logger logger() {
    return logger.getOrDefault(() -> LoggerFactory.getLogger(((F) this).getClass().getName()));
  }

  @Override
  public FunctionType getType() {
    return type.get();
  }

  @Override
  public Address getAddress(MarketMessage mm) {
    return new Address(getType(), addressResolver.get().apply(mm).toString());
  }

  @Override
  public ImmutableSet<MessageTypeCase> getValidMessageTypes() {
    return validMessageTypes.get();
  }

  protected boolean isFirstMessage(MarketMessage mm) {
    if (firstMessage.get() == null) {
      firstMessage.set(mm);
      return true;
    }
    return false;
  }
}
