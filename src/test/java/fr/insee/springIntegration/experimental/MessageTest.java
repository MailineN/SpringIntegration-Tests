package fr.insee.springIntegration.experimental;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.springIntegration.experimental.config.IntegrationConfig;
import fr.insee.springIntegration.experimental.model.Unit;
import static org.junit.Assert.*;

import fr.insee.springIntegration.experimental.service.IntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageTest {
    @Autowired
    @Qualifier("integration.unit.objectToJson.channel")
    private MessageChannel someInputChannel;

    @Autowired
    IntegrationConfig integrationConfig;

    @Test
    public void testMessagePayload() {
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");


        // Build 2 messages with same header id and same payload
        Message<Unit> message1 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        Message<Unit> message2 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        assertEquals(message1.getPayload(), message2.getPayload());

    }
    @Test
    public void testMessageHeader() {
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");


        // Build 2 messages with same header id and same payload
        Message<Unit> message1 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        Message<Unit> message2 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        assertEquals(message1.getHeaders().get("idMessage"), message2.getHeaders().get("idMessage"));

    }

    @Test
    public void testSendMessage() {
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");

        // Build a message
        Message<?> message1 = MessageBuilder.withPayload(unit).build();

        ObjectToJsonTransformer transformer = new ObjectToJsonTransformer(integrationConfig.getMapper());

        this.someInputChannel.send(new GenericMessage<>(transformer.transform(message1)));
        // Ad Channel Interceptor to check if the message is sent

    }
}
