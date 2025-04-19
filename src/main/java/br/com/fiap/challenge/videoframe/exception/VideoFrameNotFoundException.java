package br.com.fiap.challenge.videoframe.exception;

import java.text.MessageFormat;

public class VideoFrameNotFoundException extends VideoFrameException {

    public VideoFrameNotFoundException(String message) {
        super(message);
    }

    public VideoFrameNotFoundException(String message, Object... parameters) {
        super(new MessageFormat(message).format(parameters));
    }

    @Override
    public Integer getCode() {
        return 404;
    }

    @Override
    public String getMessageCode() {
        return "exception.code.404";
    }
}
