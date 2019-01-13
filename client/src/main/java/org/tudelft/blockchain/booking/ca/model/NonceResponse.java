package org.tudelft.blockchain.booking.ca.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.tudelft.blockchain.booking.ca.util.ResultDeserializer;

import java.io.Serializable;
import java.util.List;


public class NonceResponse implements Serializable {
    private List<String> errors;
    private List<String> messages;
    private boolean success;

    @JsonDeserialize(using = ResultDeserializer.class)
    private Result result;

    public NonceResponse() {
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
