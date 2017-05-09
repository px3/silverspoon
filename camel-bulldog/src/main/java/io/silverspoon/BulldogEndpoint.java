package io.silverspoon;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;

/**
 * Represents a Bulldog endpoint.
 *
 * @author <a href="mailto:pavel.macik@gmail.com">Pavel Macík</a>
 * @author <a href="mailto:pipistik.bunciak@gmail.com">Štefan Bunčiak</a>
 */
@UriEndpoint(scheme = "bulldog", title = "Bulldog", syntax = "bulldog://(gpio|spi|i2c|pwm)(\\?[\\w=&%_]+)?", consumerClass = BulldogConsumer.class)
public class BulldogEndpoint extends DefaultEndpoint {

   public static final String URI_PATTERN_STRING = "bulldog://(gpio|spi|i2c|pwm)(\\?[\\w=&%_]+)?";
   public static final Pattern URI_PATTERN = Pattern.compile(URI_PATTERN_STRING);

   private static final Logger LOG = LoggerFactory.getLogger(BulldogEndpoint.class);

   private final Board board;

   @UriPath
   private String pin = null;// valid for gpio and pwm

   @UriPath
   private String value = null; // valid for gpio and pwm

   @UriPath
   private long pulseInMicroseconds = 0L; // valid for gpio

   @UriPath
   private int readLength = 0; // valid for i2c (number of bytes to read from I2C bus)

   @UriPath
   private boolean batch = false; // valid i2c (wheather the message is a single I2C message or a batch of messages

   private String bus = null;

   public static final String FEATURE_GPIO = "gpio";
   public static final String FEATURE_SPI = "spi";
   public static final String FEATURE_I2C = "i2c";
   public static final String FEATURE_PWM = "pwm";

   private String feature = null;

   public BulldogEndpoint(String uri, BulldogComponent component) {
      super(uri, component);

      final Matcher m = URI_PATTERN.matcher(uri);
      if (!m.matches()) {
         throw new RuntimeException("Specified URI (" + uri + ") does not match the requested pattern (" + URI_PATTERN_STRING + ")");
      }

      feature = m.group(1);

      board = Platform.createBoard();
      LOG.info("Board found: " + board.getName());
   }

   public Producer createProducer() throws Exception {
      switch (feature) {
         case FEATURE_GPIO:
            return new GpioProducer(this);
         case FEATURE_I2C:
            return new I2cProducer(this);
         case FEATURE_SPI:
         case FEATURE_PWM:
         default:
            throw new RuntimeException("Unsupported feature (" + feature + ")");
      }
   }

   public Consumer createConsumer(Processor processor) throws Exception {
      if (value != null) {
         LOG.warn("Found value for pin. Omitting, creating consumer component.");
      }
      return new BulldogConsumer(this, processor);
   }

   public boolean isSingleton() {
      return true;
   }

   public String getPin() {
      return this.pin;
   }

   public void setPin(String pin) {
      this.pin = pin;
   }

   public Board getBoard() {
      return this.board;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public int getReadLength() {
      return this.readLength;
   }

   public void setReadLength(int readLength) {
      this.readLength = readLength;
   }

   public long getPulseInMicroseconds() {
      return pulseInMicroseconds;
   }

   public void setPulseInMicroseconds(final long pulseInMicroseconds) {
      this.pulseInMicroseconds = pulseInMicroseconds;
   }

   public String getBus() {
      return bus;
   }

   public void setBus(String bus) {
      this.bus = bus;
   }

   public boolean isBatch() {
      return batch;
   }

   public void setBatch(final boolean batch) {
      this.batch = batch;
   }
}
