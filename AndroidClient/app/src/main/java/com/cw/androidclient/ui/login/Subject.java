package com.cw.androidclient.ui.login;

public class Subject {
    private long subjectId;
    private String name;
    private String comment;
    private String authorLogin;
    private double totalSum;
    private boolean isActive;

    public Subject(long subjectId, String name, String comment, String authorLogin, double totalSum, boolean isActive) {
        this.subjectId = subjectId;
        this.name = name;
        this.comment = comment;
        this.authorLogin = authorLogin;
        this.totalSum = totalSum;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
