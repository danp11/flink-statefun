package org.apache.flink.statefun.examples.stockmarket.surveillance;

import org.apache.flink.statefun.examples.stockmarket.protocol.generated.MarketMessage;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

public final class FileSupplier implements Supplier<MarketMessage> {

  private File file;
  private Supplier<MarketMessage> ingressSupplier;

  public static FileSupplier fromPath(String filePath) {
    return new FileSupplier(filePath);
  }

  private FileSupplier(String filePath) {
    this.file = FileUtils.getFile(Objects.requireNonNull(filePath));
    this.ingressSupplier = BaseSupplier.fromStrings(getIterator());
  }

  private Iterator<String> getIterator() {
    try {
      BufferedReader reader = getFileReader();
      return reader.lines().sequential().iterator();
    } catch (IOException e) {
      throw new RuntimeException("Error reading records from file: " + file.getAbsolutePath(), e);
    }
  }

  private BufferedReader getFileReader() throws IOException {
    try (InputStream is = getInputStream()) {

      // Todo messure performance with diffrent buffer size
      return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
  }

  private InputStream getInputStream() throws IOException {
    InputStream is = new FileInputStream(file);
    if (isGzFile()) {
      is = new GZIPInputStream(is);
    }
    return is;
  }

  private boolean isGzFile() {
    return file.getName().endsWith(".gz");
  }

  @Override
  public MarketMessage get() {
    return ingressSupplier.get();
  }
}
