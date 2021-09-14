package com.cw.androidclient.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final long clientId;
    private final String displayName;

    public LoggedInUser(long clientId, String displayName) {
        this.clientId = clientId;
        this.displayName = displayName;
    }

    public long getClientId() {
        return clientId;
    }

    public String getDisplayName() {
        return displayName;
    }
}