package fr.insee.springIntegration.experimental;

import fr.insee.springIntegration.experimental.model.Unit;
import fr.insee.springIntegration.experimental.service.IntegrationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.Assert.assertEquals;

public class IdempotentReceiverTest {

    @Autowired
    private IntegrationService integrationService;

    @Test
    public void testIdempotentReceiver() {
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");


        // Build 2 messages with same header id and same payload
        Message<Unit> message1 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        Message<Unit> message2 = MessageBuilder.withPayload(unit).setHeader("idMessage", "testHeader").build();
        // Send messages to integration.unit.objectToJson.channel
        Message<?> sendMessage1 = integrationService.dummyMessageSender(message1);
        Message<?> sendMessage2 = integrationService.dummyMessageSender(message2);
        // Check if the filter is active

    }
}
