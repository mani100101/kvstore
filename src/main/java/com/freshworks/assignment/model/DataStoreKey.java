package com.freshworks.assignment.model;

/**
 * @author Manikandan E
 */
public class DataStoreKey {

    private final String key;

    private final long timeToLive;

    private final long createTime;

    public DataStoreKey(String key, long timeToLive) {
        this.key = key;
        this.timeToLive = timeToLive;
        this.createTime = System.currentTimeMillis();
    }

    public String key() {
        return key;
    }

    public long timeToLive() {
        return timeToLive;
    }

    public boolean expired() {
        return (this.createTime + this.timeToLive) <= System.currentTimeMillis();
    }
}
