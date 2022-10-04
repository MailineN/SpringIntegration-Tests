package fr.insee.springIntegration.experimental;

import fr.insee.springIntegration.experimental.config.IntegrationConfig;
import fr.insee.springIntegration.experimental.model.Unit;
import static org.junit.Assert.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.history.MessageHistory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageTest {

    private Logger logger = LogManager.getLogger(MessageTest.class);
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
    public void messageHistoryTest() throws Exception {
        final AbstractMessageChannel messageChannel;
        final Message<String> inputMessage;
        final List<Message> subscriberReceivedMessages =
                new CopyOnWriteArrayList<>();


        messageChannel = new DirectChannel();
        messageChannel.setComponentName("messageChannel");
        messageChannel.setShouldTrack(true);

        // Add suscriber
        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((DirectChannel)messageChannel).subscribe(subscriber);

        // send message
        inputMessage = MessageBuilder.withPayload("Hello World").build();
        messageChannel.send(inputMessage);

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                subscriberReceivedMessages.size() > 0);

        final Message<String> theFirstReceivedMessage = subscriberReceivedMessages.get(0);
        final MessageHistory theFirstReceivedMessageHistory =
                MessageHistory.read(theFirstReceivedMessage);
        final Properties messageHistoryEntry = theFirstReceivedMessageHistory.get(0);

        logger.info("Message history object: " + theFirstReceivedMessageHistory);
        logger.info("Message history entry: " + messageHistoryEntry);

        assertEquals("Message history entry should be for our message channel",
                "messageChannel", messageHistoryEntry.getProperty("name"));
    }

    @Test
    public void loggingTest() {
        final AbstractMessageChannel messageChannel;
        final Message<String> inputMessage;
        final List<Message> subscriberReceivedMessages =
                new CopyOnWriteArrayList<>();

        /* Create the message channel. */
        messageChannel = new DirectChannel();
        messageChannel.setComponentName("messageChannel");

        /* Enable logging for the message channel. */
        messageChannel.setLoggingEnabled(true);


        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((DirectChannel)messageChannel).subscribe(subscriber);

        inputMessage = MessageBuilder.withPayload("Hello World").build();
        messageChannel.send(inputMessage);
        messageChannel.send(inputMessage);

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                subscriberReceivedMessages.size() >= 2);

        /* No verification of the log output is made. */
    }
}
