package io.silverspoon;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * The Temperature consumer.
 */
public class TemperatureConsumer extends ScheduledPollConsumer {

   private final TemperatureEndpoint endpoint;

   public TemperatureConsumer(TemperatureEndpoint endpoint, Processor processor) {
      super(endpoint, processor);
      this.endpoint = endpoint;
   }

   @Override
   protected int poll() throws Exception {
      Exchange exchange = endpoint.createExchange();

      // create a message body
      exchange.getIn().setBody(endpoint.getTemperature());

      try {
         // send message to next processor in the route
         getProcessor().process(exchange);
         return 1; // number of messages polled
      } finally {
         // log exception if an exception occurred and was not handled
         if (exchange.getException() != null) {
            getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
         }
      }
   }
}
