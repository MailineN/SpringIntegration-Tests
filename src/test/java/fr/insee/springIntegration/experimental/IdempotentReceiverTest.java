package fr.insee.springIntegration.experimental;

import fr.insee.springIntegration.experimental.model.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.integration.MessageRejectedException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.ExpressionEvaluatingMessageProcessor;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.GenericMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IdempotentReceiverTest {
    @Test
    public void testIdempotentReceiver() {

        ConcurrentMetadataStore store = new SimpleMetadataStore();
        final AbstractMessageChannel messageChannel;
        final AbstractMessageChannel discardChannel;
        final List<Message> subscriberReceivedMessages = new CopyOnWriteArrayList<>();
        final List<Message> subscriberDiscardMessages = new CopyOnWriteArrayList<>();
        AtomicReference<Message<?>> handled = new AtomicReference<>();
        // Je ne sais pas pourquoi on a besoin de ça, mais sans ça fait une erreur
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);


        //Create Unit

        Unit unit = new Unit();
        unit.setEmail("dschrir0@europa.eu");
        unit.setNom("Schrir");
        unit.setPrenom("Dorise");
        unit.setId("1");

        // Create IdempotentReceiverInterceptor
        ExpressionEvaluatingMessageProcessor<String> idempotentKeyStrategy =
                new ExpressionEvaluatingMessageProcessor<>(new SpelExpressionParser().parseExpression("payload"));
        idempotentKeyStrategy.setBeanFactory(beanFactory);
        IdempotentReceiverInterceptor idempotentReceiverInterceptor =
                new IdempotentReceiverInterceptor(new MetadataStoreSelector(idempotentKeyStrategy, store));
        idempotentReceiverInterceptor.setThrowExceptionOnRejection(true);

        // Create message & dicard Channel
        messageChannel = new DirectChannel();
        messageChannel.setComponentName("messageIdempotentReceiverChannel");

        MessageHandler idempotentReceiver = handled::set;
        ProxyFactory proxyFactory = new ProxyFactory(idempotentReceiver);
        proxyFactory.addAdvice(idempotentReceiverInterceptor);

        idempotentReceiver = (MessageHandler) proxyFactory.getProxy();

        // Create subscriber
        final MessageHandler subscriber = subscriberReceivedMessages::add;
        ((DirectChannel) messageChannel).subscribe(subscriber);

        // Build 2 messages with same payload
        Message<Unit> message1 = MessageBuilder.withPayload(unit).build();
        Message<Unit> message2 = MessageBuilder.withPayload(unit).build();
        assertEquals(message1.getPayload(), message2.getPayload());

        // Send messages to messageIdempotentReceiverChannel
        idempotentReceiver.handleMessage(new GenericMessage<>("foo"));
        assertNotNull(store.get("foo"));


        try {
            idempotentReceiver.handleMessage(new GenericMessage<>("foo"));
            fail("MessageRejectedException expected");
        }
        catch (Exception e) {
            assertThat(e).isInstanceOf(MessageRejectedException.class);
        }
        // A finir


    }


}
