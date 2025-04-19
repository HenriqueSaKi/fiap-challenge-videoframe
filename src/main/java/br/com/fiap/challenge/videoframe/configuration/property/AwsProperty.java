package br.com.fiap.challenge.videoframe.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "application.aws", ignoreUnknownFields = false)
public record AwsProperty(
        URI endpointOverride,
        String regionOverride) {
}
