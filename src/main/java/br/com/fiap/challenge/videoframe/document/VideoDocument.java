package br.com.fiap.challenge.videoframe.document;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Document(collection = "videos")
public class VideoDocument {
    @Id
    private UUID id;

    private String username;
    private String videoName;
    private String frameName;

    private Type type;
    private Status status;

    public enum Type {
        PROCESS_VIDEO
    }

    public enum Status {
        WAITING, PROCESSING, ERROR, DONE
    }
}
