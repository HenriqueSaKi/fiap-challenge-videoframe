package br.com.fiap.challenge.videoframe.handler;

import br.com.fiap.challenge.videoframe.document.VideoDocument;
import br.com.fiap.challenge.videoframe.exception.VideoFrameBadRequestException;
import br.com.fiap.challenge.videoframe.exception.VideoFrameNotFoundException;
import br.com.fiap.challenge.videoframe.model.Video;
import br.com.fiap.challenge.videoframe.repository.VideoRepository;
import br.com.fiap.challenge.videoframe.utils.ExceptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VideoFrameHandler implements MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(VideoFrameHandler.class);

    private static final String BUCKET_IN = "bucket-videos-fiap-hackathon";
    private static final String BUCKET_OUT = "bucket-videos-fiap-hackathon";

    private final ObjectMapper objectMapper;
    private final VideoRepository videoRepository;

    private final S3Client s3Client;

    @Transactional
    @Override
    public void handle(Message<String> message) {
        Video value;

        try {
            value = objectMapper.readValue(message.getPayload(), Video.class);
        } catch (JsonProcessingException e) {
            throw ExceptionUtil.internalServerError(e, "Error converting video to JSON");
        }

        LOG.debug("Retrieving video information with id: {} and type {}", value.getFrameId(), value.getType());
        VideoDocument videoDocument = videoRepository.findById(value.getFrameId())
                .orElseThrow(() -> new VideoFrameNotFoundException("O video com id {0} não foi encontrao na base de dados", value.getFrameId()));

        if (ObjectUtils.notEqual(VideoDocument.Status.PROCESSING, videoDocument.getStatus())) {
            throw new VideoFrameBadRequestException(
                    "O vídeo só pode ser processado se estiver com o status {0}", VideoDocument.Status.PROCESSING);
        }

        try (var inputStream = s3Client.getObject(
                GetObjectRequest.builder().bucket(BUCKET_IN).key("videos/" + value.getKey()).build());
             var c = new Java2DFrameConverter();
             var baos = new ByteArrayOutputStream()) {

            try (var zipOutputStream = new ZipOutputStream(baos)) {
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
                frameGrabber.start();

                long videoLengthInMicroseconds = frameGrabber.getLengthInTime(); // in microseconds
                long intervalInMicroseconds = 1_000_000; // 1 second

                for (long timestamp = 0; timestamp < videoLengthInMicroseconds; timestamp += intervalInMicroseconds) {
                    frameGrabber.setTimestamp(timestamp);
                    Frame frame = frameGrabber.grabImage(); // Grab only video frames

//                    if (frame == null) {
//                        LOG.debug("No frame at timestamp {}", timestamp);
//                        continue;
//                    }

                    BufferedImage image = c.convert(frame);
//                    if (image == null) {
//                        LOG.debug("Frame at timestamp {} is empty", timestamp);
//                        continue;
//                    }

                    String fileName = String.format("Frame_%06d.png", timestamp / 1_000_000);
                    LOG.debug("Saving frame {}", fileName);
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));
                    ImageIO.write(image, "png", zipOutputStream);
                }

                frameGrabber.stop();
            }

            var fileName = videoDocument.getId() + ".zip";
            s3Client.putObject(PutObjectRequest.builder().bucket(BUCKET_OUT)
                    .key("zipFiles/" + fileName).build(), RequestBody.fromBytes(baos.toByteArray()));

            LOG.debug("Zip file {} created", fileName);

        } catch (IOException ioe) {
            throw ExceptionUtil.internalServerError(ioe, "Error extracting frames");
        }

        videoDocument.setStatus(VideoDocument.Status.DONE);

        videoRepository.save(videoDocument);
    }

}