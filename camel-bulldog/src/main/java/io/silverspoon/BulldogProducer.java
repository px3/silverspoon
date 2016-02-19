package io.silverspoon;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * The Bulldog producer.
 * 
 * @author <a href="mailto:pavel.macik@gmail.com">Pavel Macík</a>
 * @author <a href="mailto:pipistik.bunciak@gmail.com">Štefan Bunčiak</a>
 */
public abstract class BulldogProducer extends DefaultProducer {

   private BulldogEndpoint endpoint;

   public BulldogProducer(BulldogEndpoint endpoint) {
      super(endpoint);
      this.endpoint = endpoint;
   }

   public abstract void process(Exchange exchange) throws Exception;

   public BulldogEndpoint getEndpoint() {
      return this.endpoint;
   }
}
