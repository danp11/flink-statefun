package org.apache.flink.statefun.examples.stockmarket.protocol.trait;

import org.apache.flink.statefun.examples.stockmarket.protocol.constants.Constants;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;

public interface HasKafkaPartition {

  default int getPartition(MarketMessage mm) {
    int hash = getKafkaKey(mm).hashCode();
    return hash % Constants.NUMBER_OF_PROCESSORS_IN_SYSTEM;
  }

  default String getKafkaKey(MarketMessage mm) {
    MarketMessage.MessageTypeCase messageType = mm.getMessageTypeCase();

    String key;
    switch (messageType) {
      case INSTRUMENT:
        key = mm.getInstrument().getId();
        break;
      case PARTICIPANT:
        key = mm.getParticipant().getId();
        break;
      case TRADE:
        key = mm.getTrade().getInstrumentId();
        break;
      case STATECHANGE:
        key = mm.getStateChange().getInstrumentId();
        break;
      case ORDER:
        key = mm.getOrder().getInstrumentId();
        break;
      default:
        throw new IllegalArgumentException("Unknown messageType " + messageType.name());
    }
    return key;
  }
}
