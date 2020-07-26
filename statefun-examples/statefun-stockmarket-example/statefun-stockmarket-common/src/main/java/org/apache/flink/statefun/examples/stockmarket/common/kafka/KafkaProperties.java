package org.apache.flink.statefun.examples.stockmarket.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.Properties;

// Todo move to simulator module

final class KafkaProperties {
  private KafkaProperties() {}

  static final String KAFKA_SERVER = "localhost:9092";
  static final String INCOMING_TOPIC_NAME = "incoming";
  static final String ALERT_TOPIC_NAME = "alert";

  static Properties getKafkaProperties() {
    Properties kafkaProps = new Properties();
    kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER);
    kafkaProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
    kafkaProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
    return kafkaProps;
  }
}
