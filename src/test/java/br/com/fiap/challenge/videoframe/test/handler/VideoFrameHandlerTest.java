package br.com.fiap.challenge.videoframe.test.handler;

import br.com.fiap.challenge.videoframe.document.VideoDocument;
import br.com.fiap.challenge.videoframe.exception.VideoFrameBadRequestException;
import br.com.fiap.challenge.videoframe.exception.VideoFrameInternalServerErrorException;
import br.com.fiap.challenge.videoframe.exception.VideoFrameNotFoundException;
import br.com.fiap.challenge.videoframe.listener.SqsMessageListener;
import br.com.fiap.challenge.videoframe.repository.VideoRepository;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.challenge.videoframe.listener.SqsMessageListener.RASTREIO_ID;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class VideoFrameHandlerTest {
    private static final Logger LOG = LoggerFactory.getLogger(VideoFrameHandlerTest.class);

    @Autowired
    private SqsMessageListener videoFrameHandler;

    @MockBean
    private VideoRepository videoRepository;
    @MockBean
    private S3Client s3Client;

    private final String messageValue = """
            {
                "id": "b261e550-5cd8-427d-94b3-2163d9e3bea2",
                "key": "20250331_192454.mp4"
            }
            """;

    private final Message<String> json= new Message<>() {
        @Override
        @NonNull
        public String getPayload() {
            return messageValue;
        }

        @Override
        @NonNull
        public MessageHeaders getHeaders() {
            return new MessageHeaders(Map.ofEntries(
                    Map.entry(RASTREIO_ID, UUID.randomUUID().toString())
            ));
        }
    };

    @Test
    void whenSendAMessage_thenThrowErrorMessageNull() {
        Assertions.assertThrows(NullPointerException.class, () -> videoFrameHandler.onMessage((Message<String>) null));
    }

    @Test
    void whenSendAMessage_thenThrowVideoFrameInternalServerErrorException() {
        var errorJson = new Message<String>() {
            @Override
            @NonNull
            public String getPayload() {
                return "{ErrorMessageforTest}";
            }

            @Override
            @NonNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(Map.ofEntries(
                        Map.entry(RASTREIO_ID, UUID.randomUUID().toString())
                ));
            }
        };

        var error = Assertions.assertThrows(VideoFrameInternalServerErrorException.class, () -> videoFrameHandler.onMessage(errorJson));
        Assertions.assertEquals("Error converting video to JSON", error.getMessage());

        LOG.debug("Json generated for test: {}", error.toJsonObject());
    }

    @Test
    void whenSendAMessage_thenThrowVideoFrameNotFoundException() {
        var exception = Assertions.assertThrows(VideoFrameNotFoundException.class, () -> videoFrameHandler.onMessage(json));
        Assertions.assertEquals(404, exception.getCode());
        Assertions.assertEquals("exception.code.404", exception.getMessageCode());
    }

    @Test
    void whenSendAMessage_thenThrowVideoFrameBadRequestException() {
        when(videoRepository.findById(any(String.class))).then(answer -> {
            var document = new VideoDocument();
            document.setId(answer.getArgument(0, String.class));
            document.setVideoName("Frame Test 123");
            document.setStatus(VideoDocument.Status.DONE);
            document.setUsername("user.test");

            return Optional.of(document);
        });

        var json = new Message<String>() {
            @Override
            @NonNull
            public String getPayload() {
                return messageValue;
            }

            @Override
            @NonNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(Map.ofEntries(
                        Map.entry(RASTREIO_ID, UUID.randomUUID().toString())
                ));
            }
        };

        var exception = Assertions.assertThrows(VideoFrameBadRequestException.class, () -> videoFrameHandler.onMessage(json));
        Assertions.assertEquals(400, exception.getCode());
        Assertions.assertEquals("exception.code.400", exception.getMessageCode());
    }

    @Test
    void whenGetAMessage_thenThrowVideoFrameInternalServerErrorException() {
        when(videoRepository.findById(any(String.class))).then(answer -> {
            var document = new VideoDocument();
            document.setId(answer.getArgument(0, String.class));
            document.setVideoName("Frame Test 123");
            document.setStatus(VideoDocument.Status.PROCESSING);
            document.setUsername("user.test");

            return Optional.of(document);
        });

        when(s3Client.getObject(any(GetObjectRequest.class))).then(answer -> {
            throw new IOException("Error Test download media in bucket");
        });

        Assertions.assertThrows(VideoFrameInternalServerErrorException.class, () -> videoFrameHandler.onMessage(json));
    }

    @Test
    void whenGetAMessage_thenProcessWithSuccessfully() {
        var id = UUID.randomUUID();

        var document = new VideoDocument();
        document.setId(String.valueOf(id));
        document.setVideoName("Frame Test 123");
        document.setStatus(VideoDocument.Status.PROCESSING);
        document.setUsername("user.test");

        when(videoRepository.findById(any(String.class))).then(answer -> Optional.of(document));

        when(s3Client.getObject(any(GetObjectRequest.class))).then(
                answer -> {
                    var inputStream = VideoFrameHandlerTest.class.getResourceAsStream("/assets/video/SampleVideo_1280x720_1mb.mp4");
                    var objectResponse = GetObjectResponse.builder();

                    return new ResponseInputStream<>(objectResponse, inputStream);
                });

        videoFrameHandler.onMessage(json);

        Assertions.assertNotNull("Successfully");
    }
}
