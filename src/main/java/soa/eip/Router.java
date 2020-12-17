package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .process(
        new Processor(){
          public void process(Exchange ex) throws Exception {
            String body = ex.getIn().getBody(String.class);
            String[] tokens = body.split(" ");

            String newBody = "";
            String max = "";
            for(String t : tokens){
              if(t.matches("max:[0-9]+")){
                max = "?count=" + t.substring(4);
              }else{
                newBody += t;
              }
            }

            ex.getOut().setBody(newBody + max);
          }
        }
      )
      .log("Searching twitter for \"${body}\"!")
      .toD("twitter-search:${body}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
