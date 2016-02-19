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

   @Produce(uri = "direct:start")
   protected ProducerTemplate template;

   @Test
   public void testTemperature() throws Exception {
      resultEndpoint.expectedMinimumMessageCount(1);
      resultEndpoint.expectedBodiesReceived("26.0");

      template.sendBody("");

      assertMockEndpointsSatisfied();
   }

   @Override
   protected RouteBuilder createRouteBuilder() throws Exception {
      return new RouteBuilder() {
         public void configure() {
            from("direct:start").to("temperature:spi").to("mock:result");
         }
      };
   }
}
