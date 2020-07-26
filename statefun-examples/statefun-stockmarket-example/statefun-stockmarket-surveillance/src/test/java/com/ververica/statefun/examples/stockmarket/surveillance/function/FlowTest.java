package org.apache.flink.statefun.examples.stockmarket.surveillance.function;

import org.apache.flink.statefun.examples.stockmarket.generated.Instrument;
import org.apache.flink.statefun.examples.stockmarket.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.generated.Participant;
import org.apache.flink.statefun.examples.stockmarket.surveillance.ProtobufSupplier;
import org.apache.flink.statefun.examples.stockmarket.surveillance.TestRunner;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FlowTest {

  @Before
  public void setup() throws Exception {
    var inst1 = createInstrument("AMZN", "Amazon");
    var inst2 = createInstrument("FB", "Facebook");

    var marketMessages = List.of(inst1, inst2);

    TestRunner.withSupplier(ProtobufSupplier.fromCollection(marketMessages)).run();

    Thread.sleep(30000);
    KafkaProducer kafkaProducer = new KafkaProducer();
    kafkaProducer.accept(marketMessages.get(0));
    kafkaProducer.accept(marketMessages.get(1));

    // TestRunner.withSupplier(ProtobufSupplier.fromCollection(marketMessages)).run();
    /* TestRunner.withKafkaProducer(PojoSupplier.fromCollection(trades)).run();*/
  }

  private MarketMessage createInstrument(String id, String name) {
    return MarketMessage.newBuilder()
        .setInstrument(Instrument.newBuilder().setId(id).setName(name))
        .build();
  }

  private MarketMessage createParticipant(String id, String name) {
    return MarketMessage.newBuilder()
        .setParticipant(Participant.newBuilder().setId(id).setName(name).build())
        .build();
  }

  @Test
  public void testNumberOfInvocations() {}
}
