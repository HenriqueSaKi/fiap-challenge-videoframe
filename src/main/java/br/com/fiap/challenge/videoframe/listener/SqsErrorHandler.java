package br.com.fiap.challenge.videoframe.listener;

import io.awspring.cloud.sqs.listener.errorhandler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SqsErrorHandler implements ErrorHandler<String> {
    private static final Logger LOG = LoggerFactory.getLogger(SqsErrorHandler.class);

    @Override
    public void handle(Message<String> message, Throwable t) {
        LOG.error("Error processing message: {}", message.getPayload(), t);
    }
}
