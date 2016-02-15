package io.silverspoon;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import java.util.Map;

/**
 * Represents the component that manages {@link TemperatureEndpoint}.
 */
public class TemperatureComponent extends UriEndpointComponent {

   public TemperatureComponent() {
      super(TemperatureEndpoint.class);
   }

   protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
      TemperatureEndpoint endpoint = new TemperatureEndpoint(uri, remaining, this);
      setProperties(endpoint, parameters);
      return endpoint;
   }
}
