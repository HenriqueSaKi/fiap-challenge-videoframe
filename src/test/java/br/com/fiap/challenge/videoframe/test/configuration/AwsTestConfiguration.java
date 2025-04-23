package br.com.fiap.challenge.videoframe.test.configuration;

import br.com.fiap.challenge.videoframe.configuration.property.BrokerProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class AwsTestConfiguration {

    @Bean
    public SqsAsyncClient sqsAsyncClient(BrokerProperty brokerProperty) {
        var sqsClient = mock(SqsAsyncClient.class);

        receiveSetup(sqsClient, brokerProperty);

        return sqsClient;
    }

    private void receiveSetup(SqsAsyncClient sqsAsyncClient, BrokerProperty brokerProperty) {
        Message message = Message.builder().body("testBody").receiptHandle("testHandle").build();
        CompletableFuture<GetQueueUrlResponse> queueUrlResponse = CompletableFuture.completedFuture(
                GetQueueUrlResponse.builder().queueUrl(brokerProperty.from()).build());

        CompletableFuture<ReceiveMessageResponse> receiveMessage =
                CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder()
                                .messages(new ArrayList<>(Collections.singletonList(message)))
                                .build());
        ReceiveMessageRequest testRequest =
                ReceiveMessageRequest.builder()
                        .queueUrl("testUrl")
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(60)
                        .build();
        when(sqsAsyncClient.receiveMessage(testRequest)).thenReturn(receiveMessage);
        when(sqsAsyncClient.getQueueUrl(any(GetQueueUrlRequest.class))).thenReturn(queueUrlResponse);
    }


}
