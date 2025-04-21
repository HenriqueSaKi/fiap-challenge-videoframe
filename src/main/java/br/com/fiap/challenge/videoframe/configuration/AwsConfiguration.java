package br.com.fiap.challenge.videoframe.configuration;

import br.com.fiap.challenge.videoframe.configuration.property.AwsProperty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sts.StsClient;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Configuration

public class AwsConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AwsConfiguration.class);

    private final AwsProperty awsProperty;
    
    @Bean
    StsClient stsClient() {
        return applyCommonConfig(StsClient.builder(), "stsClient").build();
    }

    @Bean
    SqsClient sqsClient() {
        return applyCommonConfig(SqsClient.builder(), "sqsClient").build();
    }

    @Bean
    S3Client s3Client() {
        return applyCommonConfig(S3Client.builder(), "s3Client").build();
    }

    private <K extends AwsClientBuilder<K, V>, V> AwsClientBuilder<K, V> applyCommonConfig(
            AwsClientBuilder<K, V> builder, String name) {
        LOG.info("Setup default aws credential provider in {}", name);
        builder.credentialsProvider(DefaultCredentialsProvider.create());

        final var endpointOverride = awsProperty.endpointOverride();

        if ("stsClient".equals(name)) {
            builder.credentialsProvider(ProfileCredentialsProvider.create());
        }

        if (ObjectUtils.isNotEmpty(endpointOverride) && "sqsClient".equals(name)) {
            LOG.info("Endpoint Override '{}' in {}", endpointOverride, name);
            builder.endpointOverride(endpointOverride);
        }

        if (ObjectUtils.isNotEmpty(awsProperty.regionOverride())) {
            builder.region(Region.of(awsProperty.regionOverride()));
        }

        return builder;
    }
}
