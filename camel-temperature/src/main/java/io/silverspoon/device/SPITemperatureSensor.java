package io.silverspoon.device;

import java.io.IOException;
import java.util.List;

import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.io.bus.spi.SpiBus;
import io.silverspoon.bulldog.core.io.bus.spi.SpiConnection;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;
import io.silverspoon.bulldog.devices.sensors.*;

public class SPITemperatureSensor implements TemperatureSensor {

   private final Board board;
   private String SPI_MOSI;

   private List<SpiBus> spiBuses;
   private SpiBus spiBus;
   private DigitalOutput output;
   private SpiConnection connection;
   private LM74TemperatureSensor temperatureSensorLM74;

   /**
    * Constructor
    * 
    * @param pin sets SPI MOSI pin defined by environment variable named "SPI_MOSI".
    *        e.g.: export SPI_MOSI="P1_19"
    */
   public SPITemperatureSensor(String pin) {
      this.SPI_MOSI = pin;
      // init board
      board = Platform.createBoard();
   }

   @Override
   public float readTemperature() throws IOException {
      spiBuses = board.getSpiBuses();

      spiBus = spiBuses.get(0);

      output = board.getPin(SPI_MOSI).as(DigitalOutput.class);

      connection = spiBus.createSpiConnection(output);

      temperatureSensorLM74 = new LM74TemperatureSensor(connection, output);

      return temperatureSensorLM74.readTemperature();
   }

}
