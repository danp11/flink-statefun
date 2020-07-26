package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.sdk.StatefulFunction;

public interface Fn<F>
    extends StatefulFunction, HasType<F>, HasAddress, HasBinder<F>, HasMessageSubscription {}
