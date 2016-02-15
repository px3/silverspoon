package io.silverspoon.device;

import java.io.IOException;

/**
 * Simple temperature  sensor interface.
 * 
 * @author sbunciak
 *
 */
public interface TemperatureSensor {

   /**
    * Read temperature from a sensor.
    * 
    * @return temperature retrieved from the sensor
    * @throws IOException in case sensor is not responding
    */
   public float readTemperature() throws IOException;
   
}
