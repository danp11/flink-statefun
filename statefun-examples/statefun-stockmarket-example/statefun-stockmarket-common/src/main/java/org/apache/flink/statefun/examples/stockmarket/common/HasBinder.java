package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.RequestMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.SelfMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.SubscribeMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.UnSubscribeMessage;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.match.MatchBinder;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface HasBinder<F> {

  default void isSelfMessage(MatchBinder mb, BiConsumer<Context, SelfMessage> action) {
    mb.predicate(SelfMessage.class, action);
  }

  default void isSubscribeMessage(MatchBinder mb, BiConsumer<Context, SubscribeMessage> action) {
    mb.predicate(SubscribeMessage.class, action);
  }

  default void isUnSubscribeMessage(
      MatchBinder mb, BiConsumer<Context, UnSubscribeMessage> action) {
    mb.predicate(UnSubscribeMessage.class, action);
  }

  default void isMarketMessage(MatchBinder mb, BiConsumer<Context, MarketMessage> action) {
    bind(mb, x -> true, action);
  }

  default void isRequestMessage(MatchBinder mb, BiConsumer<Context, RequestMessage> action) {
    mb.predicate(RequestMessage.class, action);
  }

  default void isTrade(MatchBinder mb, BiConsumer<Context, MarketMessage> action) {
    bind(mb, MarketMessage::hasTrade, action);
  }

  default void isOrder(MatchBinder mb, BiConsumer<Context, MarketMessage> action) {
    bind(mb, MarketMessage::hasOrder, action);
  }

  default void isInstrumentHalted(MatchBinder mb, BiConsumer<Context, MarketMessage> action) {
    bind(mb, (MarketMessage mm) -> mm.hasStateChange() && mm.getStateChange().isHalted(), action);
  }

  default void isInstrumentClosed(MatchBinder mb, BiConsumer<Context, MarketMessage> action) {
    bind(mb, (MarketMessage mm) -> mm.hasStateChange() && mm.getStateChange().isClosed(), action);
  }

  default void otherwise(MatchBinder mb, BiConsumer<Context, Object> action) {
    mb.otherwise(action);
  }

  default void bind(
      MatchBinder mb, Predicate<MarketMessage> p, BiConsumer<Context, MarketMessage> action) {
    mb.predicate(MarketMessage.class, p, action);
  }

  default void throwException(Context context, Object message) {
    throw new RuntimeException(
        "Unnecessary processing of message "
            + "("
            + message
            + ") was received in function"
            + ((F) this).getClass()
            + "... Think about the performance please ;-)");
  }
}
