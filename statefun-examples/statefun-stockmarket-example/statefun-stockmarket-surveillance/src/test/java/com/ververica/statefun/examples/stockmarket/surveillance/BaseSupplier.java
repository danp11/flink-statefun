package org.apache.flink.statefun.examples.stockmarket.surveillance;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

class BaseSupplier<I> implements Supplier<MarketMessage> {

  private Iterator<I> iterator;
  private Function<I, MarketMessage> mapper;

  BaseSupplier() {}

  private BaseSupplier(Iterator<I> messages, Function<I, MarketMessage> mapper) {
    this.iterator = Objects.requireNonNull(messages);
    this.mapper = Objects.requireNonNull(mapper);
  }

  static BaseSupplier fromStrings(Iterator<String> messages) {
    return new BaseSupplier(messages, next -> stringToMessageParser((String) next));
  }

  static BaseSupplier fromMessages(Iterator<MarketMessage> messages) {
    return new BaseSupplier(messages, Function.identity());
  }

  @Override
  public synchronized MarketMessage get() {
    if (iterator.hasNext()) {
      return mapper.apply(iterator.next());
    } else {
      throw new NoSuchElementException("All messages red");
    }
  }

  private static MarketMessage stringToMessageParser(String next) {
    try {
      return MarketMessage.parseFrom(next.getBytes(StandardCharsets.UTF_8));
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }
}
