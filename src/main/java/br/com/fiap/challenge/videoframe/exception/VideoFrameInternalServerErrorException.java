package br.com.fiap.challenge.videoframe.exception;

public class VideoFrameInternalServerErrorException extends VideoFrameException {

    public VideoFrameInternalServerErrorException(String message) {
        super(message);
    }

    @Override
    public Integer getCode() {
        return 500;
    }

    @Override
    public String getMessageCode() {
        return "exception.code.500";
    }
}
