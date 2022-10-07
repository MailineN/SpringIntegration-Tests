package fr.insee.springIntegration.experimental;

import fr.insee.springIntegration.experimental.model.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import static org.junit.Assert.*;

public class WireTapTest {
    final Logger logger =  LogManager.getLogger(WireTapTest.class);

    @Test
    public void WireTapInterceptorTest() {
        QueueChannel messageChannel = new QueueChannel();
        QueueChannel wireTapChannel = new QueueChannel();
        messageChannel.addInterceptor(new WireTap(wireTapChannel));


        // Create a message and send it to the message channel.
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");

        Message<String> message = MessageBuilder.withPayload(unit.toString()).build();

        messageChannel.send(message);

        // Get the message from the wire tap channel.
        Message<?> original = messageChannel.receive(0);
        assertNotNull(original);
        Message<?> intercepted = wireTapChannel.receive(0);
        assertNotNull(intercepted);
        assertEquals(original, intercepted);

    }

    @Test
    public void WireTapInterceptorHeaderTest(){
        QueueChannel messageChannel = new QueueChannel();
        QueueChannel wireTapChannel = new QueueChannel();
        messageChannel.addInterceptor(new WireTap(wireTapChannel));


        // Create a message and send it to the message channel.
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");

        Message<String> message = MessageBuilder.withPayload(unit.toString()).setHeader("testHeader","idkWhatToWriteHere").build();

        messageChannel.send(message);

        String original = messageChannel.receive(0).getHeaders().get("testHeader").toString();
        String intercepted = wireTapChannel.receive(0).getHeaders().get("testHeader").toString();
        assertNotNull(intercepted);
        assertEquals(original, intercepted);

    }
}
