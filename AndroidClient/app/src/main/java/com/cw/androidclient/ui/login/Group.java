package com.cw.androidclient.ui.login;

public class Group {

    private final String name;

    private final String token;

    private final long groupId;

    public Group(String name, String token, long groupId) {
        this.name = name;
        this.token = token;
        this.groupId = groupId;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public long getGroupId() {
        return groupId;
    }
}
