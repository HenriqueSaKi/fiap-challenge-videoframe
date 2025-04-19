package br.com.fiap.challenge.videoframe.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.annotations.NotNull;

@ConfigurationProperties(prefix = "application.broker", ignoreUnknownFields = false)
public record BrokerProperty(
        @NotNull String from) {
}
