package br.com.fiap.challenge.videoframe.handler;

import br.com.fiap.challenge.videoframe.document.VideoDocument;
import br.com.fiap.challenge.videoframe.exception.VideoFrameInternalServerErrorException;
import br.com.fiap.challenge.videoframe.exception.VideoFrameNotFoundException;
import br.com.fiap.challenge.videoframe.model.Video;
import br.com.fiap.challenge.videoframe.repository.VideoRepository;
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

    private static final String BUCKET_IN = "bucket-videos-fiap-hackathon/videos";
    private static final String BUCKET_OUT = "bucket-videos-fiap-hackathon/zipFiles";

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
            var internalException = new VideoFrameInternalServerErrorException("Error converting video to JSON");
            internalException.setStackTrace(e.getStackTrace());

            throw internalException;
        }

        LOG.debug("Retrieving video information with id: {}", value.getFrameId());
        VideoDocument videoDocument = videoRepository.findById(value.getFrameId())
                .orElseThrow(() -> new VideoFrameNotFoundException("O video com id {0} não foi encontrao na base de dados", value.getFrameId()));

        if (ObjectUtils.notEqual(VideoDocument.Status.PENDING, videoDocument.getStatus())) {
            // TODO - throw exception aqui
        }

        try (var inputStream = s3Client.getObject(
                GetObjectRequest.builder().bucket(BUCKET_IN).key(value.getKey()).build());
             var c = new Java2DFrameConverter();
             var baos = new ByteArrayOutputStream()) {

            try (var zipOutputStream = new ZipOutputStream(baos)) {

                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
                frameGrabber.start();

                int frameCount = frameGrabber.getLengthInFrames();
                for (int frameNumber = 0; frameNumber < frameCount; frameNumber++) {
                    LOG.debug("Extracting {} of {} frames", frameNumber, frameCount);
                    Frame f = frameGrabber.grab();
                    BufferedImage bi = c.convert(f);

                    var fileName = "Frame " + String.format("%04d", frameNumber) + "-" + String.format("%04d", frameCount) + ".png";
                    if (ObjectUtils.isEmpty(bi)) {
                        LOG.debug("Frame {} is empty", fileName);
                        continue;
                    }

                    LOG.debug("Saving frame {}", fileName);
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));
                    ImageIO.write(bi, "png", zipOutputStream);
                }

                frameGrabber.stop();
            }
            var fileName = videoDocument.getId() + ".zip";
            s3Client.putObject(PutObjectRequest.builder().bucket(BUCKET_OUT)
                    .key(fileName).build(), RequestBody.fromBytes(baos.toByteArray()));

            videoDocument.setFrameName(fileName);

            LOG.debug("Zip file {} created", fileName);

        } catch (IOException ioe) {
            var internalException = new VideoFrameInternalServerErrorException("Error getting video in bucket");
            internalException.setStackTrace(ioe.getStackTrace());

            throw internalException;
        }

        videoDocument.setStatus(VideoDocument.Status.DONE);

        videoRepository.save(videoDocument);
    }

}