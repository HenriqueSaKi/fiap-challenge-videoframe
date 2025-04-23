package br.com.fiap.challenge.videoframe.configuration;

import br.com.fiap.challenge.videoframe.configuration.property.BrokerProperty;
import br.com.fiap.challenge.videoframe.listener.SqsErrorHandler;
import br.com.fiap.challenge.videoframe.listener.SqsMessageListener;
import io.awspring.cloud.sqs.listener.MessageListenerContainer;
import io.awspring.cloud.sqs.listener.SqsMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class ListenerConfiguration {

    @Profile("!test")
    @Bean
    MessageListenerContainer<String> messageListenerContainer(
            SqsAsyncClient sqsClient,
            BrokerProperty brokerProperty,
            SqsMessageListener sqsMessageListener,
            SqsErrorHandler errorHandler) {
        var container = new SqsMessageListenerContainer<String>(sqsClient);
        container.setQueueNames(brokerProperty.from());
        container.setMessageListener(sqsMessageListener);
        container.setErrorHandler(errorHandler);

        return container;
    }
}
