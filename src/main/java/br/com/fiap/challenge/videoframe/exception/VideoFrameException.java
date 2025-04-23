package br.com.fiap.challenge.videoframe.exception;

import lombok.Getter;
import lombok.Setter;

import lombok.experimental.Accessors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import java.util.LinkedList;
import java.util.List;

public abstract class VideoFrameException extends RuntimeException {
    private final List<ErrorDetail> errors;

    VideoFrameException(String message) {
        super(message);
        this.errors = new LinkedList<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends VideoFrameException> T addErrorDetail(ErrorDetail detail) {
        errors.add(detail);
        return (T) this;
    }

    public JSONObject toJsonObject() {

        final var errorList = new JSONArray();

        final var jsonObject = new JSONObject()
                .put("errors", errorList)
                .put("code", getCode())
                .put("message", this.getMessage())
                .put("messageCode", this.getMessageCode());

        for (final var errorDetail : this.errors) {
            JSONObject error = new JSONObject()
                    .put("locationType", errorDetail.locationType.name())
                    .put("domain", errorDetail.domain)
                    .put("message", errorDetail.message)
                    .put("messageCode", errorDetail.messageCode)
                    .put("location", errorDetail.location);

            errorList.put(error);
        }

        return jsonObject;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class ErrorDetail implements Serializable {

        private String domain;
        private String message;
        private String messageCode;
        private String location;

        private LocationType locationType;

        public static ErrorDetail newInstance() {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.domain = "global";

            return errorDetail;
        }

        public ErrorDetail message(String message) {
            this.message = message;
            return this;
        }
    }

    public enum LocationType {
        PARAMETER,
        BODY,
        PATH,
        INTERNAL
    }

    public abstract Integer getCode();

    public abstract String getMessageCode();
}