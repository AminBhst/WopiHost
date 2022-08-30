package ir.viratech.wopihost.entity;

import java.time.LocalDateTime;

/**
 * the lock information stored in wopi host
 *
 */
public class LockInfo {

    private String lockValue;

    private LocalDateTime createdAt;

    private boolean expired;

    public LockInfo() {
    }

    public LockInfo(String lockValue, LocalDateTime createdAt) {
        this.lockValue = lockValue;
        this.createdAt = createdAt;
    }

    public String getLockValue() {
        return lockValue;
    }

    public void setLockValue(String lockValue) {
        this.lockValue = lockValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime lockExpiresAt) {
        this.createdAt = lockExpiresAt;
    }

    public boolean isExpired() {
        return this.createdAt.plusMinutes(30).isBefore(LocalDateTime.now());
    }

}
