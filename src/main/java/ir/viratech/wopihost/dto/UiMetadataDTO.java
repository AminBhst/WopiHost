package ir.viratech.wopihost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UiMetadataDTO {

    public UiMetadataDTO(MessageDTO messageDTO) {
        this.addMessage(messageDTO);
    }

    public UiMetadataDTO() {
    }

    @JsonProperty
    private List<MessageDTO> messages = new ArrayList<>();

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void addMessage(MessageDTO messageDTO) {
        this.messages.add(messageDTO);
    }

    public void clearMessages() {
        this.messages.clear();
    }
}
