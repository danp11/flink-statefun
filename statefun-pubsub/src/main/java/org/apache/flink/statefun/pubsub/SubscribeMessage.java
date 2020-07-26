/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.statefun.pubsub;

import org.apache.flink.statefun.sdk.Address;

import java.util.Objects;

public final class SubscribeMessage {
  private final String topic;
  private final Address address;

  public SubscribeMessage(String topic, Address address) {
    this.topic = Objects.requireNonNull(topic);
    this.address = Objects.requireNonNull(address);
  }

  public String getTopic() {
    return topic;
  }

  public Address getAddress() {
    return address;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof SubscribeMessage))
      return false;
    SubscribeMessage that = (SubscribeMessage) o;
    return Objects.equals(topic, that.topic) &&
        Objects.equals(address, that.address);
  }

  @Override public int hashCode() {
    return Objects.hash(topic, address);
  }

  @Override public String toString() {
    return "SubscribeMessage{" +
        "topic='" + topic + '\'' +
        ", address=" + address +
        '}';
  }

  static final class SubscribeAckMessage {
    public static SubscribeAckMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final SubscribeAckMessage DEFAULT_INSTANCE;

    static {
      DEFAULT_INSTANCE = new SubscribeAckMessage();
    }

  }
}

