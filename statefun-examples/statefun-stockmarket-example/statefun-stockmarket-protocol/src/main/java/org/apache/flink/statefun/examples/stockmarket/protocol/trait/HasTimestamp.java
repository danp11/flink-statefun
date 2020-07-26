package org.apache.flink.statefun.examples.stockmarket.protocol.trait;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.Timestamp;

public interface HasTimestamp {

  Timestamp getTimestamp();

  default boolean isAfter(HasTimestamp other) {
    return compareTo(other) > 0;
  }

  default boolean isAfterOrSame(HasTimestamp other) {
    return compareTo(other) > -1;
  }

  default boolean isBefore(HasTimestamp other) {
    return compareTo(other) < 0;
  }

  default boolean isBeforeOrSame(HasTimestamp other) {
    return compareTo(other) < 1;
  }

  default int compareTo(HasTimestamp other) {
    if (this.getTimestamp().getSeconds() < other.getTimestamp().getSeconds()) {
      return -1;
    }

    if (this.getTimestamp().getSeconds() > other.getTimestamp().getSeconds()) {
      return 1;
    }

    return Integer.compare(this.getTimestamp().getNanos(), other.getTimestamp().getNanos());
  }
}
