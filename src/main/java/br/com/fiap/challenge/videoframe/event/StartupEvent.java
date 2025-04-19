package br.com.fiap.challenge.videoframe.event;

import br.com.fiap.challenge.videoframe.document.VideoDocument;
import br.com.fiap.challenge.videoframe.repository.VideoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StartupEvent implements ApplicationListener<ApplicationReadyEvent> {
    private final VideoRepository videoRepository;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {

        var videos = videoRepository.findAll();

        System.out.println(videos);

        /*var video = new VideoDocument();
        video.setId(UUID.randomUUID());
        video.setVideoName("Video de Teste");
        video.setType(VideoDocument.Type.PROCESS_VIDEO);
        video.setStatus(VideoDocument.Status.WAITING);
        video.setUsername("ralph.humberto");

        videoRepository.insert(video);*/
    }
}
