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

        // Link idempotent receiver interceptor to the message handler
        MessageHandler idempotentReceiver = handled::set;
        ProxyFactory proxyFactory = new ProxyFactory(idempotentReceiver);
        proxyFactory.addAdvice(idempotentReceiverInterceptor);

        idempotentReceiver = (MessageHandler) proxyFactory.getProxy();

        // Build 2 messages with same payload
        Message<String> message1 = MessageBuilder.withPayload(unit.toString()).build();
        Message<String> message2 = MessageBuilder.withPayload(unit.toString()).build();
        assertEquals(message1.getPayload(), message2.getPayload());

        // Send messages to messageIdempotentReceiverChannel
        idempotentReceiver.handleMessage(new GenericMessage<>("test"));
        assertNotNull(store.get("test"));

        // Test with the two messages
        idempotentReceiver.handleMessage(message1);
        try {
            idempotentReceiver.handleMessage(message2);
            fail("MessageRejectedException expected");
        }
        catch (Exception e) {
            assertThat(e).isInstanceOf(MessageRejectedException.class);
        }
        // A finir


    }


}
