package org.apache.flink.statefun.examples.stockmarket.surveillance;

import org.apache.flink.statefun.examples.stockmarket.common.Identifiers;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.flink.harness.Harness;
import org.apache.flink.statefun.flink.harness.io.SerializableSupplier;
import org.apache.flink.configuration.Configuration;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class TestRunner {

  private static BaseSupplier supplier;
  private static boolean isAllMessagesGenerated;

  private TestRunner(BaseSupplier supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  public static TestRunner withSupplier(BaseSupplier messageSupplier) {
    return new TestRunner(messageSupplier);
  }

  public void run() throws Exception {
    Configuration conf = new Configuration();
    conf.setBoolean(Identifiers.IS_HARNESS, true);

    Harness harness =
        new Harness()
            .noCheckpointing()
            .withConfiguration(conf)
            .withSupplyingIngress(Identifiers.TEST_INGRESS, new MessageGenerator());

    startAndWaitUntilFinished(harness);
  }

  private void startAndWaitUntilFinished(Harness harness) throws Exception {

    new Thread(
            () -> {
              try {
                harness.start();
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();

    /*  while (!isAllMessagesGenerated) {
      Thread.sleep(5000);
    }*/
  }

  private static final class MessageGenerator implements SerializableSupplier<MarketMessage> {

    private static final long serialVersionUID = 1;

    @Override
    public MarketMessage get() {
      try {
        return supplier.get();
      } catch (NoSuchElementException ex) {
        isAllMessagesGenerated = true;
        return MarketMessage.getDefaultInstance();
      }
    }
  }
}
