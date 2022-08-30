package ir.viratech.wopihost.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FileInfo implements Serializable {

    /**
     * file name
     */
    @JsonProperty("BaseFileName")
    private String baseFileName;

    /**
     * A string that uniquely identifies the owner of the file
     */
    @JsonProperty("OwnerId")
    private String ownerId;

    /**
     * File size in bytes
     */
    @JsonProperty("Size")
    private long size;


//    @JsonProperty("UserFriendlyName")
//    private String UserFriendlyName;

    @JsonProperty("UserId")
    private String userId;


    /**
     * A 256 bit SHA-2-encoded hash of the file contents, as a Base64-encoded string.
     * Used for caching purposes in WOPI clients.
     */
    @JsonProperty("SHA256")
    private String sha256;

    /**
     * The current version of the file based on the server’s file version schema
     * This value must change when the file changes, and version values must never repeat for a given file.
     */
    @JsonProperty("Version")
    private long version;

    /**
     * indicates a WOPI client may allow connections to external services referenced in the file
     */
    @JsonProperty("AllowExternalMarketplace")
    private boolean allowExternalMarketplace = true;

    /**
     * indicates that the WOPI client if allow the user to edit the file
     */
    @JsonProperty("UserCanWrite")
    private boolean userCanWrite = true;

    /**
     * if the host supports the update operations
     */
    @JsonProperty("SupportsUpdate")
    private boolean supportsUpdate = true;

    /**
     * if the host supports the GetLock operation.
     */
    @JsonProperty("SupportsGetLock")
    private boolean supportsGetLock = true;

    /**
     * indicates that the host supports the following WOPI operations:
     * Lock, Unlock, RefreshLock, UnlockAndRelock
     */
    @JsonProperty("SupportsLocks")
    private boolean supportsLocks = true;

    /**
     * user does not have sufficient permission to create new files on the WOPI server
     */
    @JsonProperty("UserCanNotWriteRelative")
    private boolean userCanNotWriteRelative = true;


    @Override
    public String toString() {
        return "FileInfo{" +
                "baseFileName='" + baseFileName + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", size=" + size +
                ", sha256='" + sha256 + '\'' +
                ", version=" + version +
                ", allowExternalMarketplace=" + allowExternalMarketplace +
                ", userCanWrite=" + userCanWrite +
                ", supportsUpdate=" + supportsUpdate +
                ", supportsGetLock=" + supportsGetLock +
                ", supportsLocks=" + supportsLocks +
                '}';
    }

}