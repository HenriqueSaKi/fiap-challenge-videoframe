package br.com.fiap.challenge.videoframe.handler;

import org.springframework.messaging.Message;

public interface MessageHandler {
    void handle(Message<String> message);
}
