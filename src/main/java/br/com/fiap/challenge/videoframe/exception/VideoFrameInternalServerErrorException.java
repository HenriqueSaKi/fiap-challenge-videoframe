package br.com.fiap.challenge.videoframe.exception;

import java.text.MessageFormat;

public class VideoFrameInternalServerErrorException extends VideoFrameException {

    public VideoFrameInternalServerErrorException(String message) {
        super(message);
    }

    public VideoFrameInternalServerErrorException(String message, Object... parameters) {
        super(new MessageFormat(message).format(parameters));
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
