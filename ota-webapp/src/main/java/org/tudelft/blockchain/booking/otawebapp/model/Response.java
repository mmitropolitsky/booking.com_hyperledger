package org.tudelft.blockchain.booking.otawebapp.model;

public class Response {
    String message;
    ResponseStatus status;

    public Response() {}

    public Response(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public enum ResponseStatus {
        SUCCESS, FAILURE
    }
}
