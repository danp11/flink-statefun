package org.apache.flink.statefun.examples.stockmarket.persister.function;

import org.apache.flink.statefun.examples.stockmarket.common.HasAddress;
import org.apache.flink.statefun.examples.stockmarket.persister.FnPersistenceBase;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.flink.statefun.examples.stockmarket.protocol.generated.SelfMessage;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.annotations.Persisted;
import org.apache.flink.statefun.sdk.match.MatchBinder;
import org.apache.flink.statefun.sdk.state.PersistedValue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase.ORDER;
import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase.STATECHANGE;
import static org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage.MessageTypeCase.TRADE;

public final class FnPersister extends FnPersistenceBase<FnPersister> {

  public FnPersister() {
    super(HasAddress.forInstrument(), ORDER, TRADE, STATECHANGE);
  }

  @Persisted
  private final PersistedValue<List> bufferedMessages = PersistedValue.of("messages", List.class);

  @Persisted
  private final PersistedValue<OutputStream> outputStream =
      PersistedValue.of("out", OutputStream.class);

  @Override
  public void configure(MatchBinder mb) {
    isMarketMessage(mb, this::whenMarketMessage);
    isSelfMessage(mb, this::whenSelf);
  }

  private void whenSelf(Context context, SelfMessage sf) {
    for (MarketMessage mm : getMessages()) {
      try {
        mm.writeTo(outputStream.get());
      } catch (IOException e) {
        throw new RuntimeException("IO Exception occurred.", e); // Todo implement retry
      }
    }

    bufferedMessages.clear();
    sendToSelf(context);
  }

  private List<MarketMessage> getMessages() {
    return bufferedMessages.getOrDefault(() -> new ArrayList());
  }

  private void whenMarketMessage(Context context, MarketMessage mm) {
    init(context, mm);
    getMessages().add(mm);
  }

  private void init(Context context, MarketMessage mm) {
    if (isFirstMessage(mm)) {
      createOutputStream("c://temp//" + context.self().id()); // InstrumentId
      sendToSelf(context);
    }
  }

  private void sendToSelf(Context context) {
    context.sendAfter(Duration.ofSeconds(10), context.self(), SelfMessage.getDefaultInstance());
  }

  private void createOutputStream(String path) {
    File file = new File(path);
    try {
      outputStream.set(
          new BufferedOutputStream(
              new FileOutputStream(file))); // Todo measure with different buffer sizes
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Unable to find file with path " + path, e);
    }
  }
}
