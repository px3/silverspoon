package io.silverspoon;

import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.io.bus.spi.SpiBus;
import io.silverspoon.bulldog.core.io.bus.spi.SpiConnection;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;
import io.silverspoon.bulldog.raspberrypi.RaspiNames;
import io.silverspoon.device.LM74TemperatureSensor;
import io.silverspoon.device.TemperatureSensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;

import org.apache.log4j.Logger;

/**
 * Represents a Temperature endpoint.
 */
@UriEndpoint(scheme = "temperature", title = "Temperature Component", syntax = "temperature:type")
public class TemperatureEndpoint extends DefaultEndpoint {

   private String type = null;

   private final Board board;

   private List<TemperatureSensor> sensors = new ArrayList<TemperatureSensor>();
   private List<SpiBus> spiBuses;

   private static final Logger LOG = Logger.getLogger(TemperatureEndpoint.class);

   public TemperatureEndpoint(String uri, String type, TemperatureComponent component) {
      super(uri, component);

      this.type = type;

      // init board
      board = Platform.createBoard();
      LOG.info("Board: " + board);

      // init sensors
      loadSensors();
   }

   public Producer createProducer() throws Exception {
      return new TemperatureProducer(this);
   }

   public Consumer createConsumer(Processor processor) throws Exception {
      return new TemperatureConsumer(this, processor);
   }

   public boolean isSingleton() {
      return true;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   private void loadSensors() {
      switch (type) {
         case "w1":
            // loadW1Sensors();
            break;
         case "i2c":
            // loadI2cSensors();
            break;
         case "spi":
            loadSPISensors();
            break;
         default:
            break;
      }
   }

   private void loadSPISensors() {
      spiBuses = board.getSpiBuses();

      SpiBus spiBus = spiBuses.get(0);

      DigitalOutput output = board.getPin(RaspiNames.P1_19).as(DigitalOutput.class);
      LOG.info("Created DigitalOutput: " + output.getName());

      SpiConnection connection = spiBus.createSpiConnection(output);
      LOG.info("Created connection: " + connection.getAddress() + ", " + connection.getBus());

      sensors.add(new LM74TemperatureSensor(connection, output));
   }

   public float getTemperature() {
      float res = 0.00f;
      float tmpRes = 0.00f;

      try {
         for (TemperatureSensor sensor : sensors) {
            tmpRes += sensor.readTemperature();
         }
         res = tmpRes / sensors.size();
         LOG.info("res: " + res);
      } catch (IOException e) {
         LOG.error("Failed to count the temperature.", e);
      }

      return res;
   }
}
