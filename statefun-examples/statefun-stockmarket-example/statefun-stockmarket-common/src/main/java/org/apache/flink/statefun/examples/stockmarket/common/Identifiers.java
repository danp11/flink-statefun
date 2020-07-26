package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;

public final class Identifiers {
  private Identifiers() {}

  public static final FunctionType INSTRUMENT_CACHE =
      new FunctionType("Surveillance", "FnCacheInstrument");

  public static final FunctionType PARTICIPANT_CACHE =
      new FunctionType("Surveillance", "FnCacheParticipant");

  public static final IngressIdentifier<MarketMessage> TEST_INGRESS = // Todo egen testconstant
      new IngressIdentifier<>(MarketMessage.class, "x", "in");

  public static final String IS_HARNESS = "isHarness";
}
