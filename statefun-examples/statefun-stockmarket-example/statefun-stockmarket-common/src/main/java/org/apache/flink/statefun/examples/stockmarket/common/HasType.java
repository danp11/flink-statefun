package org.apache.flink.statefun.examples.stockmarket.common;

import org.apache.flink.statefun.sdk.FunctionType;

public interface HasType<F> extends HasNameSpace {
  FunctionType getType();

  default FunctionType createFunctionType() {
    return new FunctionType(getNameSpace(), ((F) this).getClass().getSimpleName());
  }
}
