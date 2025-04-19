package br.com.fiap.challenge.videoframe.model;

import java.util.UUID;

public record Video(
        UUID id,
        String name,
        String bucketName,
        String key) {

}
