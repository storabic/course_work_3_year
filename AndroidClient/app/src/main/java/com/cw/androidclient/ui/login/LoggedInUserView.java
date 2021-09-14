package com.cw.androidclient.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private final long clientId;
    private final String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(long clientId, String displayName) {
        this.clientId = clientId;
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }

    public long getClientId() {
        return clientId;
    }
}