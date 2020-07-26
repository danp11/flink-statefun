package org.apache.flink.statefun.examples.stockmarket.protocol.trait;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Market;

public interface HasMarket {
  Market getMarket();
}
