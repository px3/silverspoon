package io.silverspoon;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TemperatureComponentTest extends CamelTestSupport {

   @EndpointInject(uri = "mock:result")
   protected MockEndpoint resultEndpoint;

   @EndpointInject(uri = "mock:resultSPI")
   protected MockEndpoint resultEndpointSPI;

   @Produce(uri = "direct:start")
   protected ProducerTemplate template;

   @Produce(uri = "direct:startSPI")
   protected ProducerTemplate templateSPI;

   @Test
   public void testW1Temperature() throws Exception {
      resultEndpoint.expectedMinimumMessageCount(1);
      resultEndpoint.expectedBodiesReceived("23.125");

      template.sendBody("");

      assertMockEndpointsSatisfied();
   }

   @Test
   public void testSPITemperature() throws Exception {
      // TODO: SPIBus mocking is necessary.

      resultEndpointSPI.expectedMinimumMessageCount(1);
      resultEndpointSPI.expectedBodiesReceived("25.0");

      templateSPI.sendBody(resultEndpointSPI, "25.0");

      assertMockEndpointsSatisfied();
   }

   @Override
   protected RouteBuilder createRouteBuilder() throws Exception {
      return new RouteBuilder() {
         public void configure() {
            from("direct:start").to("temperature:w1").to("mock:result");

            from("direct:startSPI").to("temperature:spi").to("mock:resultSPI");

         }
      };
   }
}
