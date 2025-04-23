package br.com.fiap.challenge.videoframe.exception;

import java.text.MessageFormat;

public class VideoFrameBadRequestException extends VideoFrameException {

    public VideoFrameBadRequestException(String message, Object... parameters) {
        super(new MessageFormat(message).format(parameters));
    }


    @Override
    public Integer getCode() {
        return 400;
    }

    @Override
    public String getMessageCode() {
        return "exception.code.400";
    }
}
