package io.silverspoon;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.silverspoon.bulldog.core.io.bus.i2c.I2cBus;
import io.silverspoon.bulldog.core.io.bus.i2c.I2cConnection;
import io.silverspoon.bulldog.core.util.BulldogUtil;

/**
 * The I2C producer.
 *
 * @author <a href="mailto:pavel.macik@gmail.com">Pavel Macík</a>
 */
public class I2cProducer extends BulldogProducer {
   private static final Logger log = Logger.getLogger(I2cProducer.class);

   private static final Object i2cAccessLock = new Object();
   private final I2cBus i2c;
   private static final Pattern BODY_PATTERN = Pattern.compile("([0-9a-fA-F][0-9a-fA-F])*");

   public I2cProducer(BulldogEndpoint endpoint) {
      super(endpoint);
      i2c = getEndpoint().getBoard().getI2cBuses().get(0);
   }

   public void process(Exchange exchange) throws Exception {
      final Message message = exchange.getIn();
      final String address = (String) message.getHeader("address");
      final Long batchDelay = (Long) message.getHeader("batchDelay");

      final Object rawBody = message.getBody();
      final StringBuffer response = new StringBuffer();

      if (rawBody != null) {
         final String body = (String) rawBody;
         if (getEndpoint().isBatch()) {
            final String[] batchLines = body.split("\n");
            for (String batchLine : batchLines) {
               response.append(send(address, batchLine, batchDelay));
               response.append("\n");
            }
         } else {
            response.append(send(address, body));
         }
      } else {
         response.append(send(address, ""));
      }

      exchange.getIn().setBody(response.toString());
   }

   private String send(final String address, final String msg) throws Exception {
      return send(address, msg, 0L);
   }

   private String send(final String address, final String msg, final Long delay) throws Exception {
      if (address == null) {
         final String[] parts = msg.split(";");
         if (delay != null && delay > 0) {
            BulldogUtil.sleepMs(delay);
         }
         return sendI2c(parts[0], parts[1]);
      } else {
         return sendI2c(address, msg);
      }
   }

   private String sendI2c(final String address, final String msg) throws Exception {
      final Matcher bodyMatcher = BODY_PATTERN.matcher(msg);
      if (!bodyMatcher.matches()) {
         throw new CamelException("I2C message [" + msg + "] is invalid. It should be a sequence of hexadecimal character pairs.");
      }
      final int length = getEndpoint().getReadLength();
      final byte[] buffer = new byte[length];
      if (log.isTraceEnabled()) {
         log.trace("Initializing I2C connection to address: " + address);
      }

      synchronized (i2cAccessLock) {
         final I2cConnection connection = i2c.createI2cConnection(Byte.decode(address));
         try {
            byte[] requestBuffer = new byte[msg.length() / 2];

            if (log.isTraceEnabled()) {
               log.trace("Preparing I2C message");
            }

            for (int i = 0; i < requestBuffer.length; i++) {
               final String value = "0x" + msg.substring(2 * i, 2 * (i + 1));
               requestBuffer[i] = Integer.decode(value).byteValue();
               if (log.isTraceEnabled()) {
                  log.trace("Appending byte: " + value);
               }
            }
            if (log.isTraceEnabled()) {
               log.trace("Sending I2C message...");
            }
            connection.writeBytes(requestBuffer);
            if (log.isTraceEnabled()) {
               log.trace("I2C message sent");
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("Unable to write values to I2C bus (" + i2c.getName() + ") at address " + address + "!");
         }
         try {
            if (length > 0) {
               if (log.isTraceEnabled()) {
                  log.trace("Recieving I2C response: ");
               }
               connection.readBytes(buffer);
               final StringBuffer response = new StringBuffer();
               for (int i = 0; i < length; i++) {
                  response.append(String.format("%02X", buffer[i]));
               }
               if (log.isTraceEnabled()) {
                  log.trace(response);
               }
               return response.toString();
            } else {
               return "OK";
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("Unable to read values from I2C bus (" + i2c.getName() + ") at address " + address + "!");
         }
      }
   }
}
