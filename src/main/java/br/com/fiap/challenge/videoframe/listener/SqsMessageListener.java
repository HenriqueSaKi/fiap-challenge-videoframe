package br.com.fiap.challenge.videoframe.listener;

import br.com.fiap.challenge.videoframe.handler.MessageHandler;
import io.awspring.cloud.sqs.listener.MessageListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SqsMessageListener implements MessageListener<String> {
  private final MessageHandler messageHandler;

  public static final String RASTREIO_ID = "RASTREIO_ID";

  @Override
  public void onMessage(@NonNull Message<String> message) {
    MDC.put("ID_RASTREIO",
        ObjectUtils.defaultIfNull((String) message.getHeaders().get(RASTREIO_ID), UUID.randomUUID().toString()));

    messageHandler.handle(message);
  }
}
