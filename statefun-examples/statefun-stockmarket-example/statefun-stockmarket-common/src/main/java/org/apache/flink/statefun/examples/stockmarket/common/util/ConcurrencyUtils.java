package org.apache.flink.statefun.examples.stockmarket.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

public class ConcurrencyUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ConcurrencyUtils.class);

  private ConcurrencyUtils() {}

  public static <A> CompletableFuture<Collection<A>> executeInParallel(
      Collection<A> col, Consumer<A> consumer) {
    return executeInParallel(col, toFunction(consumer));
  }

  public static <A, B> CompletableFuture<Collection<B>> executeInParallel(
      Collection<A> col, Function<A, B> mapFunction) {
    CompletableFuture[] cfs = col.toArray(new CompletableFuture[0]);= col.toArray(new CompletableFuture[0]);

    return CompletableFuture.allOf(cfs)
        .thenApply(
            unused ->
                col.stream()
                    .map(async(mapFunction))
                    .filter(Objects::nonNull)
                    .map(CompletableFuture::join)
                    .collect(toList()));
  }

  private static <A, B> Function<A, CompletableFuture<B>> async(Function<A, B> mapFunction) {
    return a ->
        supplyAsync(() -> mapFunction.apply(a))
            .exceptionally(
                ex -> {
                  LOG.error("Exception caught: ", ex);
                  return null;
                });
  }

  private static <A> Function<A, A> toFunction(Consumer<A> consumer) {
    return t -> {
      consumer.accept(t);
      return t;
    };
  }
}
