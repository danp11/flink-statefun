package org.apache.flink.statefun.examples.stockmarket.protocol.trait;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.StateChange.State.*;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.StateChange;

public interface HasStateChange {

  StateChange.State getState();

  default boolean isOpened() {
    return getState() == OPENED;
  }

  default boolean isHalted() {
    return getState() == HALTED;
  }

  default boolean isClosed() {
    return getState() == CLOSED;
  }
}
