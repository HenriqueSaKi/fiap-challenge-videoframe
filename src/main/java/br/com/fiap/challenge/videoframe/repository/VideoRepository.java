package br.com.fiap.challenge.videoframe.repository;

import br.com.fiap.challenge.videoframe.document.VideoDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends MongoRepository<VideoDocument, UUID> {
}
