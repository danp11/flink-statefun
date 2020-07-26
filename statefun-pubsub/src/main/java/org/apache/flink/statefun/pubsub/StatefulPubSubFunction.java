package org.apache.flink.statefun.pubsub;

import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.match.MatchBinder;
import org.apache.flink.statefun.sdk.match.StatefulMatchFunction;
import org.apache.flink.statefun.sdk.state.PersistedValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class StatefulPubSubFunction extends StatefulMatchFunction {

  static final FunctionType TYPE = new FunctionType("org.apache.flink.statefun.sdk.pubsub", "StatefulPubSubFunction");
  static final String SELF_ID = "pubsub";
  static final Address ADDRESS = new Address(TYPE, SELF_ID);

  static final int PARTITIONS = 1000;

  @Persisted
  private final PersistedValue <Map> subscribersPerTopic = PersistedValue.of("subscribersPerTopic", Map.class);

  @Persisted
  private final PersistedValue <Map> partitionIdsPerTopic = PersistedValue.of("partitionIdsPerTopic", Map.class);

  @Override
  public void configure(MatchBinder binder) {
    binder
        .predicate(SubscribeMessage.class, this::subscribe)
        .predicate(PublishMessage.class, this::publish);
  }

  private void subscribe(Context context, SubscribeMessage subscriber) {
    if (isSelfCall(context)) {
      persistSubscriber(subscriber);
    } else {
      sendToPartition(context, subscriber);
      persistPartitionId(subscriber);
    }
  }

  private void persistSubscriber(SubscribeMessage subscriber) {
    Map <String, Set <SubscribeMessage>> map = getSubscriberMap();
    map.computeIfAbsent(subscriber.getTopic(), unused -> new LinkedHashSet <>()).add(subscriber);
    subscribersPerTopic.set(map);
  }

  private void sendToPartition(Context context, SubscribeMessage subscriber) {
    Address partitionAddress = createPartitionAddress(subscriber);
    context.send(partitionAddress, subscriber);
  }

  private void persistPartitionId(SubscribeMessage subscriber) {
    String topic = subscriber.getTopic();
    String partitionId = createPartitionAddress(subscriber).id();

    Map <String, Set <String>> partitionIdsMap = getPartitionIdsMap();
    partitionIdsMap.computeIfAbsent(topic, unused -> new LinkedHashSet <>()).add(partitionId);

    partitionIdsPerTopic.set(partitionIdsMap);
  }




  private Set <SubscribeMessage> getSubscribersStream(String topic) {
    Set <SubscribeMessage> subscribers = getSubscriberMap().get(topic);
    return subscribers != null ? subscribers : Collections.EMPTY_SET;
  }

  private Map <String, Set <SubscribeMessage>> getSubscriberMap() {
    return subscribersPerTopic.getOrDefault(new LinkedHashMap <>());
  }

  private Set <String> getPartitionIdsStream(String topic) {
    Set <String> partitionIds = getPartitionIdsMap().get(topic);
    return partitionIds != null ? partitionIds : Collections.EMPTY_SET;
  }

  private Map <String, Set <String>> getPartitionIdsMap() {
    return partitionIdsPerTopic.getOrDefault(new LinkedHashMap());
  }

  private void publish(Context context, PublishMessage message) {
    if (isSelfCall(context)) {
      publishToFunctions(context, message);
    } else {
      publishToPartitions(context, message);
    }
  }

  private void publishToPartitions(Context context, PublishMessage message) {
    getPartitionIdsStream(message)
        .map(this::createPartitionAddress)
        .forEachOrdered(partitionAddress -> context.send(partitionAddress, message));
  }

  private void publishToFunctions(Context context, PublishMessage message) {
    getSubscribersStream(message)
        .map(SubscribeMessage::getAddress)
        .forEachOrdered(functionAddress -> context.send(functionAddress, message));
  }

  private Stream <SubscribeMessage> getSubscribersStream(PublishMessage message) {
    return getSubscribersStream(message.getTopic()).stream();
  }

  private Stream <String> getPartitionIdsStream(PublishMessage message) {
    return getPartitionIdsStream(message.getTopic()).stream();
  }

  private void subscribeSucceed(Context context, SubscribeMessage.SubscribeAckMessage message) {
    //Maybe Log
  }

  private boolean isSelfCall(Context context) {
    return context.caller().type().equals(TYPE);
  }

  private Address createPartitionAddress(SubscribeMessage subscriber) {
    return createPartitionAddress(subscriber.getAddress());
  }

  private Address createPartitionAddress(Address address) {
    int partitionId = address.hashCode() % PARTITIONS;
    return createPartitionAddress(SELF_ID + "::" + partitionId);
  }

  private Address createPartitionAddress(String partitionId) {
    return new Address(TYPE, partitionId);
  }
}
