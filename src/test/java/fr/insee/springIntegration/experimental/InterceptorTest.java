package fr.insee.springIntegration.experimental;

import static org.junit.Assert.*;
import fr.insee.springIntegration.experimental.config.DummyInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InterceptorTest {
    private Logger logger = LogManager.getLogger(InterceptorTest.class);

    @Test
    public void dummyInterceptorTest() {

        Random random = new Random();

        int nvMessages = random.ints(5, 10)
                .findFirst()
                .getAsInt();

        final AbstractMessageChannel messageChannel;
        final DummyInterceptor dummyInterceptor;
        final List<Message> subscriberReceivedMessages = new CopyOnWriteArrayList<>();

        // Create the message channel.
        messageChannel = new DirectChannel();
        messageChannel.setComponentName("messageChannelName");

        // Create a channel interceptor and add it to the interceptors of the message channel.
        dummyInterceptor = new DummyInterceptor();
        messageChannel.addInterceptor(dummyInterceptor);



        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((DirectChannel)messageChannel).subscribe(subscriber);

        for (int i = 0; i < nvMessages; i++) {
            Message<String> message = MessageBuilder.withPayload("Message no" + i).build();
            messageChannel.send(message);
        }

        await().atMost(5, TimeUnit.SECONDS).until(() ->
                subscriberReceivedMessages.size() >= nvMessages);

        assertEquals(
                "PreSend :",
                nvMessages,
                dummyInterceptor.getmPreSendMessageCount().intValue());
        assertEquals(
                "AfterSendCompletion : ",
                nvMessages,
                dummyInterceptor.getmAfterSendCompletionMessageCount().intValue());

        assertEquals(
                "Number of messages : ",
                nvMessages,
                subscriberReceivedMessages.size());

    }
}
