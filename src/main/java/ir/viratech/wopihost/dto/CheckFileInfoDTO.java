package ir.viratech.wopihost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * This DTO is consumed by office online and is not of use to the client application
 */
@Setter
@Getter
public class CheckFileInfoDTO {

    @JsonProperty("BaseFileName")
    private String baseFileName;

    @JsonProperty("OwnerId")
    private String ownerId;

    @JsonProperty("Size")
    private Long size;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("UserFriendlyName")
    private String UserFriendlyName;

    @JsonProperty("UserCanWrite")
    private boolean userCanWrite;

    @JsonProperty("ReadOnly")
    private boolean readOnly;

    @JsonProperty("SupportsLocks")
    private boolean supportsLocks;

    @JsonProperty("SupportsUpdate")
    private boolean supportsUpdate;

    @JsonProperty("UserCanNotWriteRelative")
    private boolean userCanNotWriteRelative;

    public CheckFileInfoDTO() {
        this.userCanWrite = true;
        this.readOnly = false;
        this.supportsLocks = false;
        this.supportsUpdate = true;
        this.userCanNotWriteRelative = true;
    }
}
