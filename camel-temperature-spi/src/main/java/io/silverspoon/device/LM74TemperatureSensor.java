package io.silverspoon.device;

import java.io.IOException;

import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.io.bus.spi.SpiBus;
import io.silverspoon.bulldog.core.io.bus.spi.SpiConnection;
import io.silverspoon.bulldog.core.io.bus.spi.SpiDevice;
import io.silverspoon.bulldog.core.io.bus.spi.SpiMessage;
import io.silverspoon.bulldog.core.util.BulldogUtil;

/**
 * Texas Instruments LM74 temperature sensor implementation.
 * 
 * @author matejperejda
 * @see {@link TemperatureSensor}
 *
 */
public class LM74TemperatureSensor extends SpiDevice implements TemperatureSensor {

   private DigitalOutput digitalOutput = null;

   /**
    * This constructor is disabled. It can be used just if DigitalOutput is
    * initialized.
    * 
    * @param connection
    */
   private LM74TemperatureSensor(SpiConnection connection) {
      super(connection);
   }

   /**
    * This constructor is disabled. It can be used just if DigitalOutput is
    * initialized.
    * 
    * @param bus
    * @param address
    */
   private LM74TemperatureSensor(SpiBus bus, int address) {
      super(bus, address);
   }

   /**
    * Constructor.
    * 
    * @param connection
    * @param digitalOutput
    */
   public LM74TemperatureSensor(SpiConnection connection, DigitalOutput digitalOutput) {
      super(connection);
      this.digitalOutput = digitalOutput;
   }

   /**
    * LM74 activation signal. LM74'll be activated when signal goes from 1 to
    * 0.
    */
   public void initSensor() {
      digitalOutput.low();
      digitalOutput.toggle();
      BulldogUtil.sleepMs(500);
   }

   /**
    * Reading a temperature from sensor. Two's complement calculation.
    * 
    * @see Page 11: http://www.ti.com/lit/ds/symlink/lm74.pdf
    * @return float temperature value
    * @throws IOException
    * @see {@link TemperatureSensor#readTemperature()}
    **/
   public float readTemperature() throws IOException {

      float temperature = 0;
      int bitShift;

      this.initSensor();
      this.open();

      // sending bytes
      byte[] buffer = new byte[] {(byte) 0x00, (byte) 0x00};

      try {
         SpiMessage message = this.transfer(buffer);

         byte[] rec = message.getReceivedBytes();

         int rec_0 = rec[0] & 0xFF;

         int merged = (((BulldogUtil.getUnsignedByte(rec[0]) << 8)) | BulldogUtil.getUnsignedByte(rec[1]));
         int first = rec_0 & Integer.parseInt("80", 16);

         // negative temperature
         // first binary digit 1 represents negative value
         // 128 = b10000000
         if (first == 128) {
            int substr = merged - 1;
            short inverted = (short) ~substr;
            bitShift = inverted >> 3;
            temperature = (float) (bitShift * 0.0625 * -1f);
         }
         // positive temperature
         else {
            bitShift = merged >> 3;
            temperature = (float) (bitShift * 0.0625);
         }

         // chip shutdown
         buffer = new byte[] {(byte) 0xFF};

         this.transfer(buffer);

         return temperature;
         // return (float) Math.round(temperature);
      } catch (IOException e) {
         throw new IOException("Something went wrong! SpiMessage was not transfered.");
      } finally {
         this.close();
      }
   }
}
