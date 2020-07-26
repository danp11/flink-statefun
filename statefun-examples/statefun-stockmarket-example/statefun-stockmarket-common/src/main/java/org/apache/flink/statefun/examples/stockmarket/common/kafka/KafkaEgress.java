package org.apache.flink.statefun.examples.stockmarket.common.kafka;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.io.EgressIdentifier;
import org.apache.flink.statefun.sdk.io.EgressSpec;
import org.apache.flink.statefun.sdk.kafka.KafkaEgressBuilder;
import org.apache.flink.statefun.sdk.kafka.KafkaEgressSerializer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KafkaEgress {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaEgress.class);

  private KafkaEgress() {}

  // Todo max.in.flight.requests.per.connection garantee ordering
  public static final EgressIdentifier<MarketMessage> ID =
      new EgressIdentifier<>("functions", "output-egress", MarketMessage.class);

  public static final EgressSpec<MarketMessage> KAFKA_ALERT_EGRESS =
      KafkaEgressBuilder.forIdentifier(ID)
          .withKafkaAddress(KafkaProperties.KAFKA_SERVER)
          .withSerializer(MessageSerializer.class)
          .build();

  private static final class MessageSerializer implements KafkaEgressSerializer<MarketMessage> {

    private static final long serialVersionUID = 1;

    @Override
    public ProducerRecord<byte[], byte[]> serialize(MarketMessage mm) {
      try {
        return new ProducerRecord<>(KafkaProperties.ALERT_TOPIC_NAME, mm.toByteArray());
      } catch (Throwable t) {
        LOG.error("Failed to serialize message" + mm.toString(), t);
        return null;
      }
    }
  }
}
