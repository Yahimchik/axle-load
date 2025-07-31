package com.mehatronics.axle_load.data.repository.impl;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.helper.SingleLiveEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PasswordRepositoryImpl implements PasswordRepository {
    private final SingleLiveEvent<Void> showPasswordDialogEvent = new SingleLiveEvent<>();
    private boolean shouldSendPassword = false;
    private boolean isSet = false;
    private String password;

    @Inject
    public PasswordRepositoryImpl() {
    }

    @Override
    public void save(String password) {
        this.password = password;
        this.shouldSendPassword = true;
    }

    @Override
    public String get() {
        return password == null ? "" : password;
    }

    @Override
    public void clear() {
        password = null;
        shouldSendPassword = false;
        isSet = false;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public void setFlag(boolean flag) {
        isSet = flag;
    }

    @Override
    public boolean shouldSendPassword() {
        return shouldSendPassword;
    }

    @Override
    public void setShouldSendPassword(boolean value) {
        this.shouldSendPassword = value;
    }

    @Override
    public LiveData<Void> getShowPasswordDialogEvent() {
        return showPasswordDialogEvent;
    }

    @Override
    public void requestPasswordInput() {
        showPasswordDialogEvent.call();
    }
}