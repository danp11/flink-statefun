package org.apache.flink.statefun.examples.stockmarket.persister;

import org.apache.flink.statefun.examples.stockmarket.common.FnBase;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase;

import java.util.function.Function;

public abstract class FnPersistenceBase<F> extends FnBase<F> {

  protected FnPersistenceBase(
      Function<MarketMessage, String> addressResolver, MessageTypeCase... validMessageTypes) {
    super(addressResolver, validMessageTypes);
  }

  @Override
  public String getNameSpace() {
    return "Persistence";
  }
}
