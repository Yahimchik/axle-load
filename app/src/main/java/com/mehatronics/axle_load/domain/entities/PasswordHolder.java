package com.mehatronics.axle_load.domain.entities;

public class PasswordHolder {

    private static final PasswordHolder INSTANCE = new PasswordHolder();

    private String password = "";
    private boolean shouldSendPassword = false;
    private boolean isPasswordSet = false;

    private PasswordHolder() {
    }

    public static PasswordHolder getInstance() {
        return INSTANCE;
    }

    public void setPassword(String password, boolean send) {
        this.password = password;
        this.shouldSendPassword = send;
    }

    public String getPassword() {
        return password;
    }

    public boolean shouldSendPassword() {
        return shouldSendPassword;
    }

    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    public void setPasswordSet(boolean passwordSet) {
        isPasswordSet = passwordSet;
    }

    public void clear() {
        isPasswordSet = false;
        this.password = "";
        this.shouldSendPassword = false;
    }
}

