package io.silverspoon;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

public class BulldogComponentTest extends CamelTestSupport {

   private final String pinName = System.getProperty("test.pinName", "P1_11");
   private final String pinValue = System.getProperty("test.pinValue", "HIGH");

   @EndpointInject(uri = "mock:result")
   protected MockEndpoint mockEndpoint;

   @Produce(uri = "direct:start")
   protected ProducerTemplate template;

   @Test
   public void testGpio() throws Exception {
      MockEndpoint mock = getMockEndpoint("mock:result");
      mock.expectedMinimumMessageCount(1);

      template.sendBody("");

      assertMockEndpointsSatisfied();

      // assert pin value
      // TODO: Enable once https://github.com/px3/bulldog/issues/47 is fixed.
      /*
       * Board board = Platform.createBoard();
       * DigitalOutput outputPin = board.getPin(pinName).as(DigitalOutput.class);
       * 
       * Signal signal = Signal.fromString(pinValue);
       * assertEquals(outputPin.getAppliedSignal(), signal);
       */
   }

   @Ignore
   public void testSPI() {
      // TODO: Implement with #35
   }

   @Ignore
   public void testI2C() {
      // TODO: Implement with #36
   }

   @Ignore
   public void testPWM() {
      // TODO: Implement with #38
   }

   @Override
   protected RouteBuilder createRouteBuilder() throws Exception {
      return new RouteBuilder() {
         public void configure() {
            from("direct:start").to(String.format("bulldog:gpio?pin=%s&value=%s", pinName, pinValue)).to("mock:result");
         }
      };
   }

   /*
    * Enable debugging IDE
    */
   @Override
   public boolean isUseDebugger() {
      return true;
   }
}
