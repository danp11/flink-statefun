package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.common.kafka.KafkaIngress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.io.EgressSpec;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressSpec;
import org.apache.flink.statefun.sdk.io.Router;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public interface FnModule extends StatefulFunctionModule {

  ImmutableSet<Fn> getFunctions();

  IngressIdentifier<MarketMessage> getIngressIdentifier();

  default Router<MarketMessage> getRouter() {
    return new FnRouter(getFunctions());
  }

  default IngressSpec<MarketMessage> createIngress() {
    IngressIdentifier<MarketMessage> ingressId = getIngressIdentifier();
    return KafkaIngress.createIngressSpec(ingressId);
  }

  default Optional<EgressSpec> getEgress() {
    return empty();
  }

  @Override
  default void configure(Map<String, String> globalConfiguration, Binder binder) {
    bindIngress(globalConfiguration, binder);

    getEgress().ifPresent(binder::bindEgress);

    for (Fn function : getFunctions()) {
      binder.bindFunctionProvider(function.getType(), unused -> function);
    }
  }

  default void bindIngress(Map<String, String> globalConfiguration, Binder binder) {
    if (isHarness(globalConfiguration)) {
      binder.bindIngressRouter(Identifiers.TEST_INGRESS, getRouter());
    } else {
      binder.bindIngress(createIngress());
      binder.bindIngressRouter(getIngressIdentifier(), getRouter());
    }
  }

  default boolean isHarness(Map<String, String> conf) {
    return Boolean.parseBoolean(conf.get(Identifiers.IS_HARNESS));
  }
}
