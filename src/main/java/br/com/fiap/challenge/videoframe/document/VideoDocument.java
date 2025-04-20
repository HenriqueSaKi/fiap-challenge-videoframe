package br.com.fiap.challenge.videoframe.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "frames")
public class VideoDocument {

    @Id
    @JsonProperty("_id")
    private String id;
    private String username;
    private String email;
    private String videoName;
    private Status status;
    private String createdAt;

    public enum Status {
        PENDING, UPLOAD_FEITO, PROCESSING, ERROR, DONE
    }
}
