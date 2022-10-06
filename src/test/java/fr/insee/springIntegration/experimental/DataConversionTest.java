package fr.insee.springIntegration.experimental;

import fr.insee.springIntegration.experimental.model.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;

import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class DataConversionTest {
    private Logger logger = LogManager.getLogger(DataConversionTest.class);
    @Test
    public void DataConversionUnitTest() {
        final AbstractMessageChannel messageChannel;
        final List<Message> subscriberReceivedMessages =
                new CopyOnWriteArrayList<>();
        final GenericMessageConverter genericMessageConverter;


        // Create the message channel.
        messageChannel = new PublishSubscribeChannel();
        messageChannel.setComponentName("messageChannel");
        messageChannel.setDatatypes(Unit.class);

        // Create message from unit
        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");

        Message<Unit> message = MessageBuilder.withPayload(unit).build();

        //Create converter
        genericMessageConverter = new GenericMessageConverter();
        messageChannel.setMessageConverter(genericMessageConverter);

        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((PublishSubscribeChannel)messageChannel).subscribe(subscriber);

        //Send message
        messageChannel.send(message);
        await().atMost(2, TimeUnit.SECONDS).until(() -> subscriberReceivedMessages.size() > 0);

        assertTrue(subscriberReceivedMessages.size() > 0);

        final Message<?> outputMessage = subscriberReceivedMessages.get(0);
        final Object theOutputPayload = outputMessage.getPayload();

        logger.info("Message received: " + outputMessage);
        assertThat(outputMessage.getPayload()).isInstanceOf(Unit.class);

    }

    @Test
    public void wrongTypeTest() {
        final AbstractMessageChannel messageChannel;
        final List<Message> subscriberReceivedMessages =
                new CopyOnWriteArrayList<>();
        final Message message;

        message = MessageBuilder.withPayload("test").build();

        // Create the message channel.
        messageChannel = new DirectChannel();
        messageChannel.setComponentName("messageChannel");
        messageChannel.setDatatypes(int.class);


        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((DirectChannel) messageChannel).subscribe(subscriber);

        try {
            messageChannel.send(message);
            fail("An exception should be thrown");
        } catch (final Exception e) {
            logger.info("Exeption thrown: " + e);
            assertThat(e).isInstanceOf(MessageDeliveryException.class);
        }
    }

}
