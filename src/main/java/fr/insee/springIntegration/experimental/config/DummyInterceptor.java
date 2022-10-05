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

    private AtomicInteger mPreSendMessageCount = new AtomicInteger();
    private AtomicInteger mPostSendMessageCount = new AtomicInteger();
    private AtomicInteger mAfterSendCompletionMessageCount = new AtomicInteger();
    private AtomicInteger mPreReceiveMessageCount = new AtomicInteger();
    private AtomicInteger mPostReceiveMessageCount = new AtomicInteger();
    private AtomicInteger mAfterReceiveCompletionMessageCount = new AtomicInteger();


    @Override
    public Message<?> preSend(final Message<?> inMessage,
                              final MessageChannel inChannel) {
        logMessageWithChannelAndPayload("Pre Send : ",
                inMessage,
                inChannel,
                (Object[]) null);
        mPreSendMessageCount.incrementAndGet();
        return inMessage;
    }

    @Override
    public void postSend(final Message<?> inMessage, final MessageChannel inChannel,
                         final boolean inSent) {
        logMessageWithChannelAndPayload("Post Send : ",
                inMessage,
                inChannel,
                (Object[]) null);
        mPostSendMessageCount.incrementAndGet();
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
        mAfterSendCompletionMessageCount.incrementAndGet();
    }

    @Override
    public boolean preReceive(final MessageChannel inChannel) {
        /* Only applies to pollable channels. */
        logMessageWithChannelAndPayload("Pre Receive : ",
                null,
                inChannel,
                (Object[]) null);
        mPreReceiveMessageCount.incrementAndGet();

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
        mPostReceiveMessageCount.incrementAndGet();
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
        mAfterReceiveCompletionMessageCount.incrementAndGet();
    }

    protected void logMessageWithChannelAndPayload(final String inLogMessage,
                                                   final Message<?> inMessage,
                                                   final MessageChannel inMessageChannel,
                                                   final Object... inAdditionalInMessage) {
        if (logger.isInfoEnabled()) {
            final int theAppendMsgParamsStartIndex =
                    (inAdditionalInMessage != null) ? inAdditionalInMessage.length : 0;

            String logMessage =
                    new StringBuilder().append(inLogMessage)
                            .append(" Channel: {")
                            .append(theAppendMsgParamsStartIndex)
                            .append("}. Payload: {")
                            .append(theAppendMsgParamsStartIndex + 1)
                            .append("}")
                            .toString();

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

    public AtomicInteger getmPreSendMessageCount() {
        return mPreSendMessageCount;
    }

    public void setmPreSendMessageCount(AtomicInteger mPreSendMessageCount) {
        this.mPreSendMessageCount = mPreSendMessageCount;
    }

    public AtomicInteger getmPostSendMessageCount() {
        return mPostSendMessageCount;
    }

    public void setmPostSendMessageCount(AtomicInteger mPostSendMessageCount) {
        this.mPostSendMessageCount = mPostSendMessageCount;
    }

    public AtomicInteger getmAfterSendCompletionMessageCount() {
        return mAfterSendCompletionMessageCount;
    }

    public void setmAfterSendCompletionMessageCount(AtomicInteger mAfterSendCompletionMessageCount) {
        this.mAfterSendCompletionMessageCount = mAfterSendCompletionMessageCount;
    }

    public AtomicInteger getmPreReceiveMessageCount() {
        return mPreReceiveMessageCount;
    }

    public void setmPreReceiveMessageCount(AtomicInteger mPreReceiveMessageCount) {
        this.mPreReceiveMessageCount = mPreReceiveMessageCount;
    }

    public AtomicInteger getmPostReceiveMessageCount() {
        return mPostReceiveMessageCount;
    }

    public void setmPostReceiveMessageCount(AtomicInteger mPostReceiveMessageCount) {
        this.mPostReceiveMessageCount = mPostReceiveMessageCount;
    }

    public AtomicInteger getmAfterReceiveCompletionMessageCount() {
        return mAfterReceiveCompletionMessageCount;
    }

    public void setmAfterReceiveCompletionMessageCount(AtomicInteger mAfterReceiveCompletionMessageCount) {
        this.mAfterReceiveCompletionMessageCount = mAfterReceiveCompletionMessageCount;
    }
}
