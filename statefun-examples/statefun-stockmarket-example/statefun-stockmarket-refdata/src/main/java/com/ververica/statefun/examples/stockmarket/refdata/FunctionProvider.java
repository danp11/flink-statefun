package org.apache.flink.statefun.examples.stockmarket.refdata;

import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.StatefulFunction;
import org.apache.flink.statefun.sdk.StatefulFunctionProvider;

public class FunctionProvider implements StatefulFunctionProvider {

  @Override
  public StatefulFunction functionOfType(FunctionType type) {
    return null;
  }
}
