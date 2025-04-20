package br.com.fiap.challenge.videoframe.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  JavaTimeModule javaTimeModule() {
    return new JavaTimeModule();
  }

  @Bean
  Jdk8Module jdk8Module() {
    return new Jdk8Module();
  }

  @Bean
  ObjectMapper objectMapper(Jdk8Module jdk8Module, JavaTimeModule javaTimeModule) {
    return JsonMapper.builder()
        .addModule(jdk8Module)
        .addModule(javaTimeModule)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .serializationInclusion(JsonInclude.Include.NON_EMPTY)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .build();
  }
}
