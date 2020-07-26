package org.apache.flink.statefun.examples.stockmarket.surveillance;

import org.apache.flink.statefun.examples.stockmarket.common.FnBase;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase;

import java.util.function.Function;

public abstract class FnSurveillanceBase<F> extends FnBase<F> {

  protected FnSurveillanceBase(
      Function<MarketMessage, String> addressResolver, MessageTypeCase... validMessageTypes) {
    super(addressResolver, validMessageTypes);
  }

  @Override
  public String getNameSpace() {
    return "Surveillance";
  }
}
