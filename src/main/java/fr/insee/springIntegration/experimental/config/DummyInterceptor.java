package fr.insee.springIntegration.experimental.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyInterceptor implements ChannelInterceptor {
    // Interceptor : Intercepte les messages à plusieurs étapes de l'envoi ou de la reception, peut modifier ou stopper l'envoi
    // Logger + Counter interceptor
    private Logger logger = LogManager.getLogger(DummyInterceptor.class);

    private AtomicInteger preSendMessageCount = new AtomicInteger();
    private AtomicInteger postSendMessageCount = new AtomicInteger();
    private AtomicInteger afterSendCompletionMessageCount = new AtomicInteger();
    private AtomicInteger preReceiveMessageCount = new AtomicInteger();
    private AtomicInteger postReceiveMessageCount = new AtomicInteger();
    private AtomicInteger afterReceiveCompletionMessageCount = new AtomicInteger();


    @Override
    public Message<?> preSend(final Message<?> inMessage,
                              final MessageChannel inChannel) {
        logMessageWithChannelAndPayload("Pre Send : ",
                inMessage,
                inChannel,
                (Object[]) null);
        preSendMessageCount.incrementAndGet();
        return inMessage;
    }

    @Override
    public void postSend(final Message<?> inMessage, final MessageChannel inChannel,
                         final boolean inSent) {
        logMessageWithChannelAndPayload("Post Send : ",
                inMessage,
                inChannel,
                (Object[]) null);
        postSendMessageCount.incrementAndGet();
    }

    @Override
    public void afterSendCompletion(final Message<?> inMessage,
                                    final MessageChannel inChannel,
                                    final boolean inSent,
                                    final Exception inException) {
        logMessageWithChannelAndPayload(
                "Post SendCompletion : ",
                inMessage,
                inChannel,
                inException);
        afterSendCompletionMessageCount.incrementAndGet();
    }

    @Override
    public boolean preReceive(final MessageChannel inChannel) {
        /* Only applies to pollable channels. */
        logMessageWithChannelAndPayload("Pre Receive : ",
                null,
                inChannel,
                (Object[]) null);
        preReceiveMessageCount.incrementAndGet();

        /* Returning true means go ahead with the receive. */
        return true;
    }

    @Override
    public Message<?> postReceive(final Message<?> inMessage,
                                  final MessageChannel inChannel) {
        /* Only applies to pollable channels. */
        logMessageWithChannelAndPayload("Post Receive : ",
                null,
                inChannel,
                (Object[]) null);
        postReceiveMessageCount.incrementAndGet();
        return inMessage;
    }

    @Override
    public void afterReceiveCompletion(final Message<?> inMessage,
                                       final MessageChannel inChannel,
                                       final Exception inException) {
        logger.info(
                "After message receive completion. Channel: " + inChannel.toString()
                        + " Payload: " + inMessage.getPayload()
                        + " Exception: " + inException);
        afterReceiveCompletionMessageCount.incrementAndGet();
    }

    protected void logMessageWithChannelAndPayload(final String inLogMessage,
                                                   final Message<?> inMessage,
                                                   final MessageChannel inMessageChannel,
                                                   final Object... inAdditionalInMessage) {
        if (logger.isInfoEnabled()) {
            final int theAppendMsgParamsStartIndex =
                    (inAdditionalInMessage != null) ? inAdditionalInMessage.length : 0;

            String logMessage = " Channel: {"+ theAppendMsgParamsStartIndex +
                    "} \n \t Payload: {" + (theAppendMsgParamsStartIndex + 1) + "}";

            final Object[] logMessageParameters;
            if (inAdditionalInMessage != null) {
                logMessageParameters = Arrays.copyOf(inAdditionalInMessage,
                        inAdditionalInMessage.length + 2);
            } else {
                logMessageParameters = new Object[2];
            }

            logMessageParameters[theAppendMsgParamsStartIndex] =
                    (inMessageChannel != null)
                            ? inMessageChannel.toString() : "null message channel";
            logMessageParameters[theAppendMsgParamsStartIndex + 1] =
                    (inMessage != null) ? inMessage.getPayload()
                            : "null message";
            logMessage =
                    MessageFormat.format(logMessage, logMessageParameters);
            logger.info(logMessage);
        }}

    public AtomicInteger getpreSendMessageCount() {
        return preSendMessageCount;
    }

    public void setpreSendMessageCount(AtomicInteger preSendMessageCount) {
        this.preSendMessageCount = preSendMessageCount;
    }

    public AtomicInteger getpostSendMessageCount() {
        return postSendMessageCount;
    }

    public void setpostSendMessageCount(AtomicInteger postSendMessageCount) {
        this.postSendMessageCount = postSendMessageCount;
    }

    public AtomicInteger getafterSendCompletionMessageCount() {
        return afterSendCompletionMessageCount;
    }

    public void setafterSendCompletionMessageCount(AtomicInteger afterSendCompletionMessageCount) {
        this.afterSendCompletionMessageCount = afterSendCompletionMessageCount;
    }

    public AtomicInteger getpreReceiveMessageCount() {
        return preReceiveMessageCount;
    }

    public void setpreReceiveMessageCount(AtomicInteger preReceiveMessageCount) {
        this.preReceiveMessageCount = preReceiveMessageCount;
    }

    public AtomicInteger getpostReceiveMessageCount() {
        return postReceiveMessageCount;
    }

    public void setpostReceiveMessageCount(AtomicInteger postReceiveMessageCount) {
        this.postReceiveMessageCount = postReceiveMessageCount;
    }

    public AtomicInteger getafterReceiveCompletionMessageCount() {
        return afterReceiveCompletionMessageCount;
    }

    public void setafterReceiveCompletionMessageCount(AtomicInteger afterReceiveCompletionMessageCount) {
        this.afterReceiveCompletionMessageCount = afterReceiveCompletionMessageCount;
    }
}
