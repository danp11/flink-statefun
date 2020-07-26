package org.apache.flink.statefun.pubsub;

import org.apache.flink.statefun.sdk.Address;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.testutils.function.FunctionTestHarness;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.statefun.pubsub.StatefulPubSubFunction.*;
import static org.apache.flink.statefun.pubsub.StatefulPubSubFunction.TYPE;
import static org.apache.flink.statefun.testutils.matchers.StatefulFunctionMatchers.messagesTo;
import static org.apache.flink.statefun.testutils.matchers.StatefulFunctionMatchers.sent;
import static org.apache.flink.statefun.testutils.matchers.StatefulFunctionMatchers.sentNothing;
import static org.hamcrest.CoreMatchers.equalTo;

public class StatefulPubSubFunctionTest {

  private static final String TOPIC_1 = "topic-1";
  private static final String TOPIC_2 = "topic-2";

  private static final FunctionType SUBSCRIBER_FN = new FunctionType("test", "subscriber");

  private static final Address SUBSCRIBER_1 = new Address(SUBSCRIBER_FN, "subscriber-1");
  private static final SubscribeMessage SUBSCRIBER_1_TOPIC_1_MESSAGE = new SubscribeMessage(TOPIC_1, SUBSCRIBER_1);
  private static final SubscribeMessage SUBSCRIBER_1_TOPIC_2_MESSAGE = new SubscribeMessage(TOPIC_2, SUBSCRIBER_1);
  private static final Address SUBSCRIBER_1_FN_PARTITION = getPartitionFunction(SUBSCRIBER_1);

  private static final Address SUBSCRIBER_2 = new Address(SUBSCRIBER_FN, "subscriber-2");
  private static final SubscribeMessage SUBSCRIBER_2_TOPIC_1_MESSAGE = new SubscribeMessage(TOPIC_1, SUBSCRIBER_2);
  private static final SubscribeMessage SUBSCRIBER_2_TOPIC_2_MESSAGE = new SubscribeMessage(TOPIC_2, SUBSCRIBER_2);
  private static final Address SUBSCRIBER_2_FN_PARTITION = getPartitionFunction(SUBSCRIBER_2);




  private static final FunctionType PUBLISHER_FN = new FunctionType("test", "publisher");

  private static final Address PUBLISHER_1 = new Address(PUBLISHER_FN, "publisher-1");
  private static final PublishMessage PUBLISHER_1_TOPIC_1_MESSAGE = new PublishMessage(TOPIC_1, "message-1");


  @Test
  public void testSubscriptionRoutingToPartitions(){
    FunctionTestHarness harness = createHarnessByAddressId(SELF_ID);

    Assert.assertThat(
        "When receiving a subscribe message, the function should forward the message to a partitioned pubsub function",
        harness.invoke(SUBSCRIBER_1, SUBSCRIBER_1_TOPIC_1_MESSAGE),
        sent(messagesTo(SUBSCRIBER_1_FN_PARTITION, equalTo(SUBSCRIBER_1_TOPIC_1_MESSAGE))));

     Assert.assertThat(
        "When one function subscribes to several topics, the subscriber should be placed in the same partitioned pubsub function",
        harness.invoke(SUBSCRIBER_1, SUBSCRIBER_1_TOPIC_2_MESSAGE),
        sent(messagesTo(SUBSCRIBER_1_FN_PARTITION, equalTo(SUBSCRIBER_1_TOPIC_2_MESSAGE))));
  }

  @Test
  public void testPublishRoutingToPartitions(){
    FunctionTestHarness harness = createHarnessByAddressId(SELF_ID);

    harness.invoke(SUBSCRIBER_1, SUBSCRIBER_1_TOPIC_1_MESSAGE);
    harness.invoke(SUBSCRIBER_2, SUBSCRIBER_2_TOPIC_1_MESSAGE);

    Assert.assertThat(
        "When receiving publish messages, the function should forward the messages to the correct partitioned pubsub function",
        harness.invoke(PUBLISHER_1, PUBLISHER_1_TOPIC_1_MESSAGE),
        sent(
            messagesTo(SUBSCRIBER_1_FN_PARTITION, equalTo(PUBLISHER_1_TOPIC_1_MESSAGE)),
            messagesTo(SUBSCRIBER_2_FN_PARTITION, equalTo(PUBLISHER_1_TOPIC_1_MESSAGE)))
    );
  }

  @Test
  public void testSendNothingWhenNoSubscribers(){
    FunctionTestHarness harness = createHarnessByAddressId(SELF_ID);

    Assert.assertThat(
        "When receiving a publish message, but the topics has no subscribers, the function should not send any message",
        harness.invoke(PUBLISHER_1, PUBLISHER_1_TOPIC_1_MESSAGE), sentNothing());
  }

  @Test
  public void testPublishRoutingToFunctions() {
    FunctionTestHarness harness = createHarnessByAddressId(SUBSCRIBER_1_FN_PARTITION.id());

    harness.invoke(SUBSCRIBER_1, SUBSCRIBER_1_TOPIC_1_MESSAGE);

    Assert.assertThat(
        "When a partitioned function receives a publish message, the function should forward the message to its subscribing function",
        harness.invoke(PUBLISHER_1, PUBLISHER_1_TOPIC_1_MESSAGE),
        sent(messagesTo(SUBSCRIBER_1, equalTo(PUBLISHER_1_TOPIC_1_MESSAGE))));
  }


  private FunctionTestHarness createHarnessByAddressId(String id) {
    return FunctionTestHarness.test(ignore -> new StatefulPubSubFunction(), TYPE, id);
  }

  private static Address getPartitionFunction(Address subscribersAddress) {
    int partition = subscribersAddress.hashCode() % PARTITIONS;
    return new Address(TYPE, SELF_ID + "::" + partition);
  }
}
