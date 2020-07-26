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

import java.util.Objects;

public final class PublishMessage {
  private final String topic;
  private final Object message;

  public PublishMessage(String topic, Object message) {
    this.topic = Objects.requireNonNull(topic);
    this.message = Objects.requireNonNull(message);
  }

  public String getTopic() {
    return topic;
  }

  public Object getMessage() {
    return message;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PublishMessage))
      return false;
    PublishMessage that = (PublishMessage) o;
    return Objects.equals(topic, that.topic) &&
        Objects.equals(message, that.message);
  }

  @Override public int hashCode() {
    return Objects.hash(topic, message);
  }

  @Override public String toString() {
    return "PublishMessage{" +
        "topic='" + topic + '\'' +
        ", message=" + message +
        '}';
  }
}

