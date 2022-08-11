package ir.viratech.wopihost.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntryPointDTO {

    public EntryPointDTO(String officeOnlineUrl) {
        this.officeOnlineUrl = officeOnlineUrl;
    }

    private String officeOnlineUrl;
}
