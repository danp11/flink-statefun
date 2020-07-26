package org.apache.flink.statefun.examples.stockmarket.persister;

import com.google.auto.service.AutoService;
import org.apache.flink.statefun.examples.stockmarket.common.Fn;
import org.apache.flink.statefun.examples.stockmarket.common.FnModule;
import org.apache.flink.statefun.examples.stockmarket.common.kafka.KafkaIngress;
import org.apache.flink.statefun.examples.stockmarket.persister.function.FnPersister;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

@AutoService(StatefulFunctionModule.class)
public class PersistenceModule implements FnModule {

  @Override
  public IngressIdentifier<MarketMessage> getIngressIdentifier() {
    return KafkaIngress.createIngressIdentifier("Persistence");
  }

  @Override
  public ImmutableSet<Fn> getFunctions() {
    return ImmutableSet.of(new FnPersister());
  }
}
