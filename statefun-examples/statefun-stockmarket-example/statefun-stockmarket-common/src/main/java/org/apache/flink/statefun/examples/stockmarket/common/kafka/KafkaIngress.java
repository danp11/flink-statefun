package org.apache.flink.statefun.examples.stockmarket.common.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressSpec;
import org.apache.flink.statefun.sdk.kafka.KafkaIngressBuilder;
import org.apache.flink.statefun.sdk.kafka.KafkaIngressDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public final class KafkaIngress {
  private KafkaIngress() {}

  public static IngressIdentifier<MarketMessage> createIngressIdentifier(String nameSpace) {
    return new IngressIdentifier<>(MarketMessage.class, nameSpace, "incoming");
  }

  public static IngressSpec<MarketMessage> createIngressSpec(
      IngressIdentifier<MarketMessage> ingressId) {
    return KafkaIngressBuilder.forIdentifier(ingressId)
        .withKafkaAddress(KafkaProperties.KAFKA_SERVER)
        .withTopic(KafkaProperties.INCOMING_TOPIC_NAME)
        .withDeserializer(MessageDeserializer.class)
        .withProperty(ConsumerConfig.GROUP_ID_CONFIG, ingressId.namespace())
        .build();
  }

  private static final class MessageDeserializer
      implements KafkaIngressDeserializer<MarketMessage> {

    private static final long serialVersionUID = 1;

    @Override
    public MarketMessage deserialize(ConsumerRecord<byte[], byte[]> input) {
      try {
        return MarketMessage.parseFrom(input.value());
      } catch (InvalidProtocolBufferException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
