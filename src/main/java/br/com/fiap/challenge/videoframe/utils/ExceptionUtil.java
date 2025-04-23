package br.com.fiap.challenge.videoframe.utils;

import br.com.fiap.challenge.videoframe.exception.VideoFrameException;
import br.com.fiap.challenge.videoframe.exception.VideoFrameInternalServerErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

    public static VideoFrameInternalServerErrorException internalServerError(
            Throwable throwable, String message) {
        var internalException = new VideoFrameInternalServerErrorException(message);
        internalException.setStackTrace(throwable.getStackTrace());

        internalException.addErrorDetail(VideoFrameException.ErrorDetail.newInstance()
                .message(throwable.getMessage())
                .messageCode(internalException.getMessageCode())
                .locationType(VideoFrameException.LocationType.INTERNAL)
                .domain("global")
                .location("body"));

        return internalException;
    }

}
