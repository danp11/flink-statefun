package org.apache.flink.statefun.examples.stockmarket.common.kafka;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.trait.HasKafkaPartition;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class KafkaProducer implements Consumer<MarketMessage>, HasKafkaPartition {

  // Todo move to simulator module
  private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
  private final org.apache.kafka.clients.producer.KafkaProducer<byte[], byte[]> producer;

  public KafkaProducer() {
    this.producer =
        new org.apache.kafka.clients.producer.KafkaProducer<>(KafkaProperties.getKafkaProperties());
  }

  @Override
  public void accept(MarketMessage mm) {
    try {

      int partition = getPartition(mm);
      byte[] key = getKafkaKey(mm).getBytes(StandardCharsets.UTF_8);
      byte[] messageBytes = mm.toByteArray();

      sendRecord(partition, key, messageBytes);

    } catch (Throwable t) {
      LOG.error("Failed to serialize and send message" + mm.toString(), t);
    }
  }

  private void sendRecord(int partition, byte[] key, byte[] messageBytes) {
    producer.send(
        new ProducerRecord<>(KafkaProperties.INCOMING_TOPIC_NAME, partition, key, messageBytes));
  }
}
