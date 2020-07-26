package org.apache.flink.statefun.examples.stockmarket.protocol.constants;

public final class Constants {
  private Constants() {}

  public static final int NUMBER_OF_PROCESSORS_IN_SYSTEM =
      Runtime.getRuntime().availableProcessors();
}
