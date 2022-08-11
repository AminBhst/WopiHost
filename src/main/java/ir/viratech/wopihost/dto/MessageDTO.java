package ir.viratech.wopihost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO {

    @JsonProperty
    private String text;

    @JsonProperty
    private MessageDTO.MessageType type;

    public MessageDTO(String text, MessageDTO.MessageType type) {
        this.text = text;
        this.type = type;
    }

    @JsonProperty
    public String getText() {
        return this.text;
    }

    @JsonProperty
    public MessageDTO.MessageType getType() {
        return this.type;
    }

    public enum MessageType {
        SUCCESS,
        WARNING,
        INFO,
        ERROR;
    }
}