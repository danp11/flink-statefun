package org.apache.flink.statefun.examples.stockmarket.surveillance;

import com.google.auto.service.AutoService;
import org.apache.flink.statefun.examples.stockmarket.common.Fn;
import org.apache.flink.statefun.examples.stockmarket.common.FnModule;
import org.apache.flink.statefun.examples.stockmarket.common.kafka.KafkaIngress;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.cache.FnCacheInstrument;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.cache.FnCacheParticipant;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade.FnLargeTrade;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade.FnLargeTradeByMarketXSTO;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.largetrade.FnLargeTradeBySubClient;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.latecancellation.FnLateCancellation;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.pubsub.FnInstrumentClosedPublisher;
import org.apache.flink.statefun.examples.stockmarket.surveillance.function.pubsub.FnInstrumentHaltedPublisher;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableSet;

@AutoService(StatefulFunctionModule.class)
public class SurveillanceModule implements FnModule {

  @Override
  public IngressIdentifier<MarketMessage> getIngressIdentifier() {
    return KafkaIngress.createIngressIdentifier("Surveillance");
  }

  @Override
  public ImmutableSet<Fn> getFunctions() {
    return ImmutableSet.of(
        new FnCacheInstrument(),
        new FnCacheParticipant(),
        new FnLargeTrade(),
        new FnLargeTradeByMarketXSTO(),
        new FnLargeTradeBySubClient(),
        new FnLateCancellation(),
        new FnInstrumentHaltedPublisher(),
        new FnInstrumentClosedPublisher());
  }
}
