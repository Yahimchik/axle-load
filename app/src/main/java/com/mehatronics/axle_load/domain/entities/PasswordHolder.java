package com.mehatronics.axle_load.domain.entities;

import javax.inject.Inject;
import javax.inject.Singleton;

public class PasswordHolder {

    private static final PasswordHolder INSTANCE = new PasswordHolder();

    private String password = "";
    private boolean shouldSendPassword = false;

    private PasswordHolder() {} // приватный конструктор

    public static PasswordHolder getInstance() {
        return INSTANCE;
    }

    public void setPassword(String password, boolean send) {
        this.password = password != null ? password : "";
        this.shouldSendPassword = send;
    }

    public String getPassword() {
        return password;
    }

    public boolean shouldSendPassword() {
        return shouldSendPassword;
    }

    public void clear() {
        this.password = "";
        this.shouldSendPassword = false;
    }
}

