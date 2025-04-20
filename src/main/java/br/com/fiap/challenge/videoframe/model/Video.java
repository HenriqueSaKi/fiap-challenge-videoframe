package br.com.fiap.challenge.videoframe.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Video {

  private Type type;
  private String frameId;
  private Status status;
  private String bucketName;
  private String key;
  private String username;
  private String email;

  public enum Type {
    PROCESS_VIDEO
  }

  public enum Status {
    PENDING, UPLOAD_FEITO, ERROR, DONE
  }

}
